package de.codingair.packetmanagement;

import com.google.common.collect.HashBiMap;
import de.codingair.packetmanagement.packets.*;
import de.codingair.packetmanagement.packets.exceptions.*;
import de.codingair.packetmanagement.utils.SuccessPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DataHandler<C, P extends Proxy> {
    private final HashBiMap<Class<? extends Packet<?>>, Integer> register = HashBiMap.create();
    private int id = 0;

    protected final String channelBackend, channelProxy;
    private final ConcurrentHashMap<UUID, CompletableFuture<? extends ResponsePacket>> future = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> timeSpecific = new ConcurrentHashMap<>();
    protected final P proxy;

    private Timer timeOutTimer = new Timer("DataHandler-TimeOut");
    private boolean running = false;
    protected long timeOut = 250L;

    public DataHandler(String channelName, P proxy) {
        this.proxy = proxy;
        channelBackend = channelName + ":backend";
        channelProxy = channelName + ":proxy";

        //register standard packets
        registerPacket(SuccessPacket.class);

        //register custom packets
        registering();
        id = -1;
    }

    protected abstract void registering();

    public void registerPacket(Class<? extends Packet<?>> c) {
        if(id == -1) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        register.put(c, id++);
    }

    protected abstract void send(byte[] data, C connection);

    public void send(@NotNull Packet<?> packet, @Nullable C connection) {
        send(packet, connection, null);
    }

    private void send(@NotNull Packet<?> packet, @Nullable C connection, @Nullable UUID id) {
        processPacket(packet, id).ifPresent(data -> send(data, connection));
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @Nullable C connection) {
        return send(packet, connection, 0);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @Nullable C connection, long timeOut) {
        CompletableFuture<A> future = packet.buildFuture();
        UUID id = generateID();

        if(timeOut > 0) {
            if(!running) checkTimer(); //check timer before registering packets
            this.timeSpecific.put(id, timeOut + System.currentTimeMillis());
        }
        this.future.put(id, future);

        processPacket(packet, id).ifPresent(data -> send(data, connection));

        return future;
    }

    /**
     * Stops (if running) the active TimeOut-Timer and removes all references to timeOuts and completable futures.
     */
    public void flush() {
        if(this.running) {
            this.timeOutTimer.cancel();
            this.timeOutTimer.purge();
            this.timeOutTimer = new Timer("DataHandler-TimeOut");
            running = false;
        }

        this.timeSpecific.clear();
        this.future.clear();
    }

    private UUID generateID() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while(future.containsKey(id));
        return id;
    }

    private int getId(Class<?> c) {
        Integer id = register.get(c);
        if(id != null) return id;
        else return -1;
    }

    private Class<? extends Packet<?>> byId(int id) {
        if(id < 0) return null;
        return register.inverse().get(id);
    }

    @NotNull
    private <T> T formPacket(int id) throws UnknownPacketException, IllegalAccessException, InstantiationException {
        Class<?> c = byId(id);

        if(c == null) throw new UnknownPacketException("The packet id " + id + " is not associated with a packet class!");
        return (T) c.newInstance();
    }

    public <A extends ResponsePacket> void receive(@NotNull byte[] bytes, @Nullable C connection) throws IOException, InstantiationException, IllegalAccessException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        Packet<?> packet = formPacket(in.readUnsignedShort());
        UUID id = packet instanceof AssignedPacket ? new UUID(in.readLong(), in.readLong()) : null;
        packet.read(in);

        if(packet instanceof RequestPacket) {
            RequestPacket<? extends ResponsiblePacketHandler<RequestPacket<?, ?>, ?>, ?> ap = (RequestPacket<? extends ResponsiblePacketHandler<RequestPacket<?, ?>, ?>, ?>) packet;
            ResponsiblePacketHandler<RequestPacket<?, ?>, ?> handler = ap.getHandler(proxy);
            if(handler == null) throw new NoHandlerException(ap.getClass());

            if(handler.answer(ap, proxy)) handler.response(ap).thenAccept(response -> send(response, connection, id));
        } else if(packet instanceof ResponsePacket) {
            if(id == null) throw new NullPointerException("No id given! Cannot handle packet: " + packet.getClass());
            A rp = (A) packet;

            this.timeSpecific.remove(id);
            CompletableFuture<A> cf = (CompletableFuture<A>) this.future.remove(id);
            if(cf == null) throw new NullPointerException("No CompletableFuture given! Cannot handle response packet: " + rp.getClass());

            cf.complete(rp);
        } else {
            Packet<? extends PacketHandler<Packet<?>>> singleton = (Packet<? extends PacketHandler<Packet<?>>>) packet;
            PacketHandler<Packet<?>> handler = singleton.getHandler(proxy);
            if(handler == null) throw new NoHandlerException(singleton.getClass());

            handler.process(packet);
        }
    }

    private Optional<byte[]> processPacket(Packet<?> packet, UUID uuid) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        int id = getId(packet.getClass());
        if(id == -1) throw new UnknownPacketException(packet.getClass() + " is not registered!");

        try {
            out.writeShort(id);

            if(packet instanceof AssignedPacket) {
                if(uuid == null) throw new NullPointerException("Cannot send assigned packet without UUID: " + packet.getClass());
                out.writeLong(uuid.getMostSignificantBits());
                out.writeLong(uuid.getLeastSignificantBits());
            } else if(uuid != null) throw new UnsupportedIdException("Cannot send id (" + uuid + ") for unsupported packet class: " + packet.getClass());

            packet.write(out);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return Optional.of(stream.toByteArray());
    }

    private synchronized void checkTimer() {
        if(running) return;
        running = true;

        try {
            this.timeOutTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    timeSpecific.entrySet().removeIf(e -> {
                        if(time >= e.getValue()) {
                            CompletableFuture<?> cf = future.remove(e.getKey());
                            if(cf != null) cf.completeExceptionally(new TimeOutException("The requested packet took too long."));
                            return true;
                        } else return false;
                    });
                }
            }, timeOut, timeOut);
        } catch(IllegalStateException ex) {
            throw new HandlerAlreadyPurgedException("This handler has already been purged.");
        }
    }

    public String getChannelProxy() {
        return channelProxy;
    }

    public String getChannelBackend() {
        return channelBackend;
    }
}

package de.codingair.packetmanagement;

import com.google.common.collect.HashBiMap;
import de.codingair.packetmanagement.exceptions.*;
import de.codingair.packetmanagement.handlers.MultiLayerPacketHandler;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.AssignedPacket;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.packets.impl.*;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DataHandler<C> {
    private final HashBiMap<Class<? extends Packet>, Short> register = HashBiMap.create();
    private final HashMap<Class<? extends Packet>, PacketHandler<?>> handlers = new HashMap<>();
    private Short id = 0;

    protected final String channelBackend, channelProxy;
    protected final ConcurrentHashMap<UUID, CompletableFuture<? extends ResponsePacket>> future = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<UUID, Long> timeSpecific = new ConcurrentHashMap<>();
    protected final Proxy proxy;

    private Timer timeOutTimer = new Timer("DataHandler-TimeOut");
    private boolean running = false;
    protected long timeOut = 250L;

    public DataHandler(String channelName, Proxy proxy) {
        this.proxy = proxy;
        channelBackend = channelName + ":backend";
        channelProxy = channelName + ":proxy";

        //register standard packets
        register();

        //register custom packets
        registering();
        id = null;
    }

    private void register() {
        id = -5;
        registerPacket(SuccessPacket.class);
        registerPacket(StringPacket.class);
        registerPacket(IntegerPacket.class);
        registerPacket(BytePacket.class);
        registerPacket(BooleanPacket.class);
    }

    protected abstract void registering();

    protected abstract boolean isConnected(Direction direction);

    protected void registerPacket(@NotNull Class<? extends Packet> sending) {
        if(id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if(register.containsValue(sending)) throw new IllegalStateException("Packet already registered!");

        register.put(sending, id++);
    }

    protected <P extends Packet> void registerPacket(@NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        if(id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if(register.containsValue(receiving)) throw new IllegalStateException("Packet already registered!");

        register.put(receiving, id++);
        handlers.put(receiving, handler);
    }

    public <P extends Packet> boolean registerHandler(@NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        return handlers.putIfAbsent(receiving, handler) == null;
    }

    public boolean registerPacket(short id, @NotNull Class<? extends Packet> sending) {
        if(this.id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if(register.containsValue(sending)) throw new IllegalStateException("Packet already registered!");

        return register.putIfAbsent(sending, id) == null;
    }

    public <P extends Packet> boolean registerPacket(short id, @NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        if(this.id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if(register.containsValue(receiving)) throw new IllegalStateException("Packet already registered!");

        if(register.put(receiving, id) != null) return false;
        handlers.put(receiving, handler);
        return true;
    }

    /**
     * @param data       Byte-Array to sent.
     * @param connection Connection which maybe will be used to send the data.
     * @param direction  The direction in which we need to send our data
     */
    protected abstract void send(byte[] data, C connection, Direction direction);

    public void send(@NotNull Packet packet, @Nullable C connection, @NotNull Direction direction) {
        send(packet, connection, direction, null);
    }

    void send(@NotNull Packet packet, @Nullable C connection, @NotNull Direction direction, @Nullable UUID id) {
        processPacket(packet, id).ifPresent(data -> send(data, connection, direction));
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection, @NotNull Direction direction) {
        return send(packet, connection, direction, 0);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection, @NotNull Direction direction, long timeOut) {
        CompletableFuture<A> future = packet.buildFuture();
        processPacket(packet, registerFuture(timeOut, future)).ifPresent(data -> send(data, connection, direction));
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

    UUID generateID() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while(future.containsKey(id));
        return id;
    }

    @NotNull <P extends Packet, H extends PacketHandler<P>> H formHandler(P packet) throws NoHandlerException {
        PacketHandler<?> handler = handlers.get(packet.getClass());
        if(handler == null) throw new NoHandlerException(packet.getClass());

        try {
            return (H) handler;
        } catch(ClassCastException e) {
            throw new HandlerException("PacketHandler " + handler + " cannot be used for packet " + packet.getClass() + "!", e);
        }
    }

    @NotNull <T> T formPacket(short id) throws UnknownPacketException, IllegalAccessException, InstantiationException {
        Class<?> c = register.inverse().get(id);
        if(c == null) throw new UnknownPacketException("The packet id " + id + " is not associated with a packet class!");

        try {
            return (T) c.newInstance();
        } catch(ClassCastException e) {
            throw new PacketException("Cannot cast packet to specified object.", e);
        }
    }

    /**
     * @param bytes      Sent Byte-Array.
     * @param connection Connection which maybe will be used to send the data.
     * @param direction  Direction where we get our data from.
     */
    public <A extends ResponsePacket> void receive(@NotNull byte[] bytes, @Nullable C connection, @NotNull Direction direction) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

            Packet packet = formPacket(in.readShort());
            UUID id = packet instanceof AssignedPacket ? new UUID(in.readLong(), in.readLong()) : null;
            packet.read(in);

            if(packet instanceof ResponsePacket) receiveResponse((A) packet, id);
            else if(packet instanceof RequestPacket) {
                RequestPacket<A> ap = (RequestPacket<A>) packet;
                ResponsiblePacketHandler<RequestPacket<A>, A> handler = formHandler(ap);

                if(handler instanceof ResponsibleMultiLayerPacketHandler) {
                    ResponsibleMultiLayerPacketHandler<RequestPacket<A>, A> multi = (ResponsibleMultiLayerPacketHandler<RequestPacket<A>, A>) handler;
                    if(multi.answer(ap, proxy, direction)) {
                        try {
                            multi.response(ap, proxy, connection, direction).thenAccept(response -> send(response, connection, direction, id));
                        } catch(Escalation e) {
                            Packet escalation = e.packet();

                            if(escalation instanceof RequestPacket) {
                                //Register response to origin
                                e.future().whenComplete((response, err) -> {
                                    if(err != null) send(e.exceptional(err), connection, direction, id);
                                    else send(response, connection, direction, id);
                                });

                                if(!isConnected(e.direction())) {
                                    e.future().completeExceptionally(new NoConnectionException("No " + e.direction().name() + " connection established!"));
                                } else {
                                    //Perform escalation
                                    processPacket(e.packet(), registerFuture(e.timeOut(timeOut), e.future())).ifPresent(escalableData -> send(escalableData, connection, e.direction()));
                                }
                            } else {
                                //Perform simple escalation
                                processPacket(e.packet(), null).ifPresent(escalableData -> send(escalableData, connection, e.direction()));
                            }
                        }
                    }
                } else if(handler.answer(ap, proxy, direction)) handler.response(ap, proxy, connection, direction).thenAccept(response -> send(response, connection, direction, id));
            } else {
                PacketHandler<Packet> handler = formHandler(packet);

                if(handler instanceof MultiLayerPacketHandler) {
                    MultiLayerPacketHandler<Packet> multi = (MultiLayerPacketHandler<Packet>) handler;
                    try {
                        multi.process(packet, proxy, connection, direction);
                    } catch(Escalation e) {
                        send(e.packet(), connection, e.direction());
                    }
                } else handler.process(packet, proxy, connection, direction);
            }
        } catch(IOException e) {
            throw new MalformedPacketException("Cannot handle bytes to form packet!", e);
        } catch(IllegalAccessException | InstantiationException e) {
            throw new PacketException("Cannot create packet instance!", e);
        }
    }

    protected final <A extends ResponsePacket> void receiveResponse(A packet, UUID id) {
        this.timeSpecific.remove(id);
        CompletableFuture<?> cf = this.future.remove(id);
        if(cf == null) {
            //No CompletableFuture given! Must be a response to another handler or the future is already terminated by a time-out!
            return;
        }

        try {
            CompletableFuture<A> future = (CompletableFuture<A>) cf;
            future.complete(packet);
        } catch(ClassCastException e) {
            throw new HandlerResponseException("Response " + packet.getClass() + " does not fit to completable future " + future.getClass() + ". Check the response of your packet handler!", e);
        }
    }

    UUID registerFuture(long timeOut, @NotNull CompletableFuture<? extends ResponsePacket> future) {
        UUID id = generateID();

        if(timeOut > 0) {
            if(!running) checkTimer(); //check timer before registering packets
            this.timeSpecific.put(id, timeOut + System.currentTimeMillis());
        }
        this.future.put(id, future);

        return id;
    }

    Optional<byte[]> processPacket(@NotNull Packet packet, @Nullable UUID uuid) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        Short id = register.get(packet.getClass());
        if(id == null) throw new UnknownPacketException(packet.getClass() + " is not registered!");

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
    }

    public String getChannelProxy() {
        return channelProxy;
    }

    public String getChannelBackend() {
        return channelBackend;
    }

    public <P extends Proxy> P getProxy() {
        return (P) proxy;
    }
}

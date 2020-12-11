package de.codingair.packetmanagement;

import com.google.common.collect.HashBiMap;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.PacketHandler;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.exceptions.UnknownPacketException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DataHandler<C> {
    private final HashBiMap<Class<? extends Packet<?>>, Integer> register = HashBiMap.create();
    private int ID = 0;

    protected final String channelBackend, channelProxy;
    protected final ConcurrentHashMap<UUID, CompletableFuture<?>> future = new ConcurrentHashMap<>();

    public DataHandler(String channelName) {
        channelBackend = channelName + ":backend";
        channelProxy = channelName + ":proxy";

        registering();
        ID = -1;
    }

    protected abstract void registering();

    protected abstract void send(byte[] data, C connection);

    public void send(@NotNull Packet<?> packet, @NotNull C connection) {
        send(packet, connection, null);
    }

    private void send(@NotNull Packet<?> packet, @NotNull C connection, @Nullable UUID id) {
        processPacket(packet, id).ifPresent(data -> send(data, connection));
    }

    public <A> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @NotNull C connection) {
        CompletableFuture<A> future = packet.buildFuture();
        UUID id = generateID();

        this.future.put(id, future);
        processPacket(packet, id).ifPresent(data -> send(data, connection));

        return future;
    }

    private void registerPacket(Class<? extends Packet<?>> c) {
        if(ID == -1) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        register.put(c, ID++);
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

    public void receive(byte[] bytes, C connection) throws UnknownPacketException, IOException, InstantiationException, IllegalAccessException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        Packet<?> packet = formPacket(in.readUnsignedShort());

        UUID id = in.readBoolean() ? new UUID(in.readLong(), in.readLong()) : null;
        packet.read(in);

        if(packet instanceof RequestPacket) {
            RequestPacket<? extends ResponsiblePacketHandler<RequestPacket<?, ?>, ?>, ?> ap = (RequestPacket<? extends ResponsiblePacketHandler<RequestPacket<?, ?>, ?>, ?>) packet;
            ap.getHandler().response(ap).thenAccept(response -> send(response, connection, id));
        } else {
            Packet<? extends PacketHandler<Packet<?>>> singleton = (Packet<? extends PacketHandler<Packet<?>>>) packet;
            singleton.getHandler().process(packet);
        }
    }

    private Optional<byte[]> processPacket(Packet<?> packet, UUID uuid) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        int id = getId(packet.getClass());
        if(id == -1) throw new IllegalStateException(packet.getClass() + " is not registered!");

        try {
            out.writeShort(id);

            out.writeBoolean(uuid != null);
            if(uuid != null) {
                out.writeLong(uuid.getMostSignificantBits());
                out.writeLong(uuid.getLeastSignificantBits());
            }

            packet.write(out);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return Optional.of(stream.toByteArray());
    }

    private UUID generateID() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while(future.containsKey(id));
        return id;
    }

    public String getChannelProxy() {
        return channelProxy;
    }

    public String getChannelBackend() {
        return channelBackend;
    }
}

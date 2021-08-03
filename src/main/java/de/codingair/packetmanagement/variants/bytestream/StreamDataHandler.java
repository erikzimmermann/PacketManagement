package de.codingair.packetmanagement.variants.bytestream;

import com.google.common.collect.HashBiMap;
import de.codingair.packetmanagement.DataHandler;
import de.codingair.packetmanagement.exceptions.MalformedPacketException;
import de.codingair.packetmanagement.exceptions.PacketException;
import de.codingair.packetmanagement.exceptions.UnknownPacketException;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.packets.AssignedPacket;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.impl.*;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.FormedPacket;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public abstract class StreamDataHandler<C> extends DataHandler<C, byte[]> {
    private final HashBiMap<Class<? extends Packet>, Short> register = HashBiMap.create();
    private Short id;

    public StreamDataHandler(@NotNull String channelName, @NotNull Proxy proxy) {
        super(channelName, proxy);

        //register standard packets
        register();

        //register custom packets
        registering();

        id = null;
    }

    private void register() {
        id = -6;
        registerPacket(SuccessPacket.class);
        registerPacket(StringPacket.class);
        registerPacket(IntegerPacket.class);
        registerPacket(LongPacket.class);
        registerPacket(BytePacket.class);
        registerPacket(BooleanPacket.class);
    }

    protected abstract void registering();

    protected void registerPacket(@NotNull Class<? extends Packet> sending) {
        if (id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if (register.containsKey(sending)) throw new IllegalStateException("Packet already registered: " + sending);

        register.put(sending, id++);
    }

    protected <P extends Packet> void registerPacket(@NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        if (id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if (register.containsKey(receiving)) throw new IllegalStateException("Packet already registered: " + receiving);

        register.put(receiving, id++);
        handlers.put(receiving, handler);
    }

    public boolean registerPacket(short id, @NotNull Class<? extends Packet> sending) {
        if (this.id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if (register.containsKey(sending)) throw new IllegalStateException("Packet already registered: " + sending);

        return register.put(sending, id) == null;
    }

    public <P extends Packet> boolean registerPacket(short id, @NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        if (this.id == null) throw new IllegalStateException("Packet classes cannot be registered on runtime!");
        if (register.containsKey(receiving)) throw new IllegalStateException("Packet already registered: " + receiving);

        if (register.put(receiving, id) != null) return false;
        handlers.put(receiving, handler);
        return true;
    }

    @NotNull
    protected <T> T formPacket(short id) throws UnknownPacketException, IllegalAccessException, InstantiationException {
        Class<?> c = register.inverse().get(id);
        if (c == null) throw new UnknownPacketException("The packet id " + id + " is not associated with a packet class!");

        try {
            //noinspection unchecked
            return (T) c.getConstructor().newInstance();
        } catch (ClassCastException | NoSuchMethodException | InvocationTargetException e) {
            throw new PacketException("Cannot cast packet to specified object.", e);
        }
    }

    @Override
    public @NotNull FormedPacket convertReceivedData(byte @NotNull [] data, @Nullable C connection, @NotNull Direction direction) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

        try {
            Packet packet = formPacket(in.readShort());
            boolean future = in.readBoolean();
            UUID id = future && packet instanceof AssignedPacket ? new UUID(in.readLong(), in.readLong()) : null;
            packet.read(in);

            return new FormedPacket(packet, future, id);
        } catch (IOException e) {
            throw new MalformedPacketException("Cannot handle bytes to form packet!", e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new PacketException("Cannot create packet instance!", e);
        }
    }

    @Override
    public byte[] serializePacket(@NotNull Packet packet, boolean future, @Nullable UUID uuid) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        Short id = register.get(packet.getClass());
        if (id == null) throw new UnknownPacketException(packet.getClass() + " is not registered!");

        try {
            out.writeShort(id);
            out.writeBoolean(future); //for request packets without response (good for lazy forwarding)

            if (uuid != null) {
                out.writeLong(uuid.getMostSignificantBits());
                out.writeLong(uuid.getLeastSignificantBits());
            }

            packet.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }
}

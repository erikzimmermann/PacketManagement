package de.codingair.packetmanagement.variants;

import de.codingair.packetmanagement.DataHandler;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class OneWayDataHandler<C> extends DataHandler<C> {
    public OneWayDataHandler(String channelName, Proxy proxy) {
        super(channelName, proxy);
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return true;
    }

    protected abstract void send(byte[] data, C connection);

    @Override
    @Deprecated
    protected void send(byte[] data, C connection, Direction direction) {
        send(data, connection);
    }

    public void send(@NotNull Packet packet, @Nullable C connection) {
        super.send(packet, connection, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public void send(@NotNull Packet packet, @Nullable C connection, @NotNull Direction direction) {
        super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection) {
        return super.send(packet, connection, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection, @NotNull Direction direction) {
        return super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection, long timeOut) {
        return super.send(packet, connection, Direction.UNKNOWN, timeOut);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection, @NotNull Direction direction, long timeOut) {
        return super.send(packet, connection, direction, timeOut);
    }

    public void receive(@NotNull byte[] bytes, @Nullable C connection) {
        super.receive(bytes, connection, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> void receive(@NotNull byte[] bytes, @Nullable C connection, @NotNull Direction direction) {
        super.receive(bytes, connection, direction);
    }
}

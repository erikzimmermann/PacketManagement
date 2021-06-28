package de.codingair.packetmanagement.variants.bytestream;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class OneWaySingleConnectionStreamDataHandler extends StreamDataHandler<Object> {
    public OneWaySingleConnectionStreamDataHandler(@NotNull String channelName, @NotNull Proxy proxy) {
        super(channelName, proxy);
    }

    @Override
    @Deprecated
    protected boolean isConnected(Direction direction) {
        return true;
    }

    protected abstract void send(byte[] data);

    @Override
    @Deprecated
    protected void send(byte[] data, Object connection, Direction direction) {
        send(data);
    }

    public void send(@NotNull Packet packet) {
        super.send(packet, null, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public void send(@NotNull Packet packet, @Nullable Object connection, @NotNull Direction direction) {
        super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet) {
        return super.send(packet, null, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable Object connection, @NotNull Direction direction) {
        return super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, long timeOut) {
        return super.send(packet, null, Direction.UNKNOWN, timeOut);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable Object connection, @NotNull Direction direction, long timeOut) {
        return super.send(packet, connection, direction, timeOut);
    }

    public void receive(@NotNull byte[] bytes) {
        super.receive(bytes, null, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> void receive(@NotNull byte[] bytes, @Nullable Object connection, @NotNull Direction direction) {
        super.receive(bytes, connection, direction);
    }
}

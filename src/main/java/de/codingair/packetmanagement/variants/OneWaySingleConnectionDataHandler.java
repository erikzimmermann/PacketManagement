package de.codingair.packetmanagement.variants;

import de.codingair.packetmanagement.DataHandler;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class OneWaySingleConnectionDataHandler extends DataHandler<Object> {
    public OneWaySingleConnectionDataHandler(String channelName, Proxy proxy) {
        super(channelName, proxy);
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return true;
    }

    protected abstract void send(byte[] data);

    @Override
    @Deprecated
    protected void send(byte[] data, Object connection, Direction direction) {
        send(data);
    }

    public void send(@NotNull Packet<?> packet) {
        super.send(packet, null, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public void send(@NotNull Packet<?> packet, @Nullable Object connection, @NotNull Direction direction) {
        super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet) {
        return super.send(packet, null, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @Nullable Object connection, @NotNull Direction direction) {
        return super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, long timeOut) {
        return super.send(packet, null, Direction.UNKNOWN, timeOut);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @Nullable Object connection, @NotNull Direction direction, long timeOut) {
        return super.send(packet, connection, direction, timeOut);
    }

    public void receive(@NotNull byte[] bytes) throws IOException, InstantiationException, IllegalAccessException {
        super.receive(bytes, null, Direction.UNKNOWN);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> void receive(@NotNull byte[] bytes, @Nullable Object connection, @NotNull Direction direction) throws IOException, InstantiationException, IllegalAccessException {
        super.receive(bytes, connection, direction);
    }
}

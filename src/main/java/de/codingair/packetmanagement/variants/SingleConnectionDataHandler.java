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

public abstract class SingleConnectionDataHandler extends DataHandler<Object> {
    public SingleConnectionDataHandler(String channelName, Proxy proxy) {
        super(channelName, proxy);
    }

    protected abstract void send(byte[] data, Direction direction);

    @Override
    protected void send(byte[] data, Object connection, Direction direction) {
        send(data, direction);
    }

    public void send(@NotNull Packet<?> packet, @NotNull Direction direction) {
        super.send(packet, null, direction);
    }

    @Override
    @Deprecated
    public void send(@NotNull Packet<?> packet, @Nullable Object connection, @NotNull Direction direction) {
        super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @NotNull Direction direction) {
        return send(packet, direction, 0);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @Nullable Object connection, Direction direction) {
        return super.send(packet, connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @NotNull Direction direction, long timeOut) {
        return super.send(packet, null, direction, timeOut);
    }

    @Override
    @Deprecated
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, @Nullable Object connection, Direction direction, long timeOut) {
        return super.send(packet, connection, direction, timeOut);
    }

    public void receive(@NotNull byte[] bytes, @NotNull Direction direction) throws IOException, InstantiationException, IllegalAccessException {
        super.receive(bytes, null, direction);
    }

    @Override
    @Deprecated
    public void receive(@NotNull byte[] bytes, @Nullable Object connection, @NotNull Direction direction) throws IOException, InstantiationException, IllegalAccessException {
        super.receive(bytes, connection, direction);
    }
}

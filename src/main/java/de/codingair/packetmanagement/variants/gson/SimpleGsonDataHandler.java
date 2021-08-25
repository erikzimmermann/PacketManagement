package de.codingair.packetmanagement.variants.gson;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.packetmanagement.variants.gson.packets.Packet;
import de.codingair.packetmanagement.variants.gson.packets.RequestPacket;
import de.codingair.packetmanagement.variants.gson.packets.ResponsePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class SimpleGsonDataHandler extends GsonDataHandler<Object> {
    public SimpleGsonDataHandler(String channelName, Proxy proxy) {
        super(channelName, proxy);
    }

    @Override
    @Deprecated
    protected void send(String json, Object connection, Direction direction) {
        send(json);
    }

    protected abstract void send(String json);

    @Override
    @Deprecated
    public void receive(@NotNull String json, @Nullable Object connection, @NotNull Direction direction) {
        super.receive(json, connection, direction);
    }

    protected void receive(String json) {
        super.receive(json, null, Direction.UNKNOWN);
    }

    public void send(@NotNull Packet packet) {
        send(packet, null, Direction.UNKNOWN);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet) {
        return send(packet, null, Direction.UNKNOWN);
    }
}

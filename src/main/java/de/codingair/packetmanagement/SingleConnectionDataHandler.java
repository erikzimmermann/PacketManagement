package de.codingair.packetmanagement;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.Proxy;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class SingleConnectionDataHandler extends DataHandler<Object> {
    public SingleConnectionDataHandler(String channelName, Proxy proxy) {
        super(channelName, proxy);
    }

    protected abstract void send(byte[] data);

    @Override
    protected void send(byte[] data, Object connection) {
        send(data);
    }

    public void send(@NotNull Packet<?> packet) {
        super.send(packet, null);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet) {
        return send(packet, 0);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<?, A> packet, long timeOut) {
        return super.send(packet, null, timeOut);
    }

    public void receive(@NotNull byte[] bytes) throws IOException, InstantiationException, IllegalAccessException {
        super.receive(bytes, null);
    }
}

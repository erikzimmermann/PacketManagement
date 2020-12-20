package de.codingair.packetmanagement.exceptions;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.packets.Packet;
import org.jetbrains.annotations.NotNull;

import javax.xml.ws.Response;
import java.util.concurrent.CompletableFuture;

public class Escalation extends PacketException {
    private final PacketHandler<?> handler;
    private final Direction direction;
    private final Packet<?> forward;
    private final ResponsePacket exceptional;
    private final CompletableFuture<? extends ResponsePacket> future;
    private final long timeOut;

    public Escalation(@NotNull PacketHandler<?> handler, @NotNull Direction direction, @NotNull Packet<?> forward) {
        this.handler = handler;
        this.direction = direction;
        this.forward = forward;
        this.exceptional = null;
        this.timeOut = 0;
        this.future = null;
    }

    public <A extends ResponsePacket> Escalation(@NotNull ResponsiblePacketHandler<?, ?> handler, @NotNull Direction direction, @NotNull RequestPacket<?, A> forward, A exceptional) {
        this(handler, direction, -1, forward, exceptional);
    }

    public <A extends ResponsePacket> Escalation(@NotNull ResponsiblePacketHandler<?, ?> handler, @NotNull Direction direction, long timeOut, @NotNull RequestPacket<?, A> forward, A exceptional) {
        this.handler = handler;
        this.direction = direction;
        this.forward = forward;
        this.timeOut = timeOut;
        this.future = forward.buildFuture();
        this.exceptional = exceptional;
    }

    public PacketHandler<?> handler() {
        return handler;
    }

    public Direction direction() {
        return direction;
    }

    public Packet<?> packet() {
        return forward;
    }

    public CompletableFuture<? extends ResponsePacket> future() {
        return future;
    }

    public long timeOut(long def) {
        return timeOut == -1 ? def : timeOut;
    }

    public ResponsePacket exceptional() {
        return exceptional;
    }
}

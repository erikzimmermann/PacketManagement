package de.codingair.packetmanagement.exceptions;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.PacketSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * An escalation will be used while processing packets which must be done on a higher level.
 */
public class Escalation extends PacketException {
    private final PacketHandler<?> handler;
    private final Direction direction;
    private final Packet forward;
    private final PacketSupplier<? extends ResponsePacket> exceptional;
    private final CompletableFuture<? extends ResponsePacket> future;
    private final long timeOut;

    /**
     * @param handler     The handler that throws this escalation.
     * @param direction   The direction where we have to escalate to.
     * @param forward     The packet which will be escalated.
     */
    public Escalation(@NotNull PacketHandler<?> handler, @NotNull Direction direction, @NotNull Packet forward) {
        this.handler = handler;
        this.direction = direction;
        this.forward = forward;
        this.exceptional = null;
        this.timeOut = 0;
        this.future = null;
    }

    /**
     * @param handler     The handler that throws this escalation.
     * @param direction   The direction where we have to escalate to.
     * @param forward     The RequestPacket which will be escalated.
     * @param exceptional The PacketSupplier that will probably be executed by a TimeOutException or a NoConnectionException.
     * @param <A>         The ResponsePacket which will be sent to the origin DataHandler that starts this conversation.
     */
    public <A extends ResponsePacket> Escalation(@NotNull ResponsiblePacketHandler<?, A> handler, @NotNull Direction direction, @NotNull RequestPacket<A> forward, @NotNull PacketSupplier<A> exceptional) {
        this(handler, direction, -1, forward, exceptional);
    }

    /**
     * @param handler     The handler that throws this escalation.
     * @param direction   The direction where we have to escalate to.
     * @param timeOut     The timeout in milliseconds for this escalation.
     * @param forward     The RequestPacket which will be escalated.
     * @param exceptional The PacketSupplier that will probably be executed by a TimeOutException or a NoConnectionException.
     * @param <A>         The ResponsePacket which will be sent to the origin DataHandler that starts this conversation.
     */
    public <A extends ResponsePacket> Escalation(@NotNull ResponsiblePacketHandler<?, A> handler, @NotNull Direction direction, long timeOut, @NotNull RequestPacket<A> forward, @NotNull PacketSupplier<A> exceptional) {
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

    public Packet packet() {
        return forward;
    }

    public CompletableFuture<? extends ResponsePacket> future() {
        return future;
    }

    public long timeOut(long def) {
        return timeOut == -1 ? def : timeOut;
    }

    public ResponsePacket exceptional(Throwable err) {
        return exceptional.exceptional(err);
    }
}

package de.codingair.packetmanagement.packets;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ResponsiblePacketHandler<P extends RequestPacket<?, ?>, A extends ResponsePacket> extends PacketHandler<P> {
    @Override
    default void process(P packet) {
        throw new UnsupportedOperationException("Use response(Packet<?> packet) instead.");
    }

    /**
     * Useful for multiple listeners and just one will answer to this packet.
     *
     * @param packet Processing packet (in)
     * @param proxy Registered proxy
     * @return true if this handler can response to this packet.
     */
    default boolean answer(P packet, Proxy proxy) {
        return true;
    }

    @NotNull
    CompletableFuture<A> response(P packet);
}

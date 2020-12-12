package de.codingair.packetmanagement.packets;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ResponsiblePacketHandler<P extends RequestPacket<?, ?>, A extends ResponsePacket> extends PacketHandler<P> {
    @Override
    default void process(P packet) {
        throw new UnsupportedOperationException("Use response(Packet<?> packet) instead.");
    }

    default boolean answer(P packet) {
        return true;
    }

    @NotNull
    CompletableFuture<A> response(P packet);
}

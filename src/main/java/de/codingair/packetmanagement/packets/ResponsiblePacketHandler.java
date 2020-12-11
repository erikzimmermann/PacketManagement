package de.codingair.packetmanagement.packets;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ResponsiblePacketHandler<P extends RequestPacket<?, ?>, A extends Packet<?>> extends PacketHandler<P> {
    @Override
    default void process(P packet) {
        throw new UnsupportedOperationException("Use response(Packet<?> packet) instead.");
    }

    @NotNull
    CompletableFuture<A> response(P packet);
}

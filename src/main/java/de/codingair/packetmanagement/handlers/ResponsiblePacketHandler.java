package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ResponsiblePacketHandler<P extends RequestPacket<?>, A extends ResponsePacket> extends PacketHandler<P> {
    @Override
    @Deprecated
    default void process(@NotNull P packet, @NotNull Proxy proxy, @Nullable Object connection) {
        throw new UnsupportedOperationException("Use response(Packet<?> packet) instead.");
    }

    /**
     * Useful for multiple listeners and just one will answer to this packet.
     *
     * @param packet Processing packet (in)
     * @param proxy  Registered proxy
     * @return true if this handler can response to this packet.
     */
    default boolean answer(@NotNull P packet, @NotNull Proxy proxy) {
        return true;
    }

    @NotNull
    CompletableFuture<A> response(@NotNull P packet, @NotNull Proxy proxy, @Nullable Object connection);
}

package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ResponsiblePacketHandler<P extends RequestPacket<?>, A extends ResponsePacket> extends PacketHandler<P> {
    @Override
    @Deprecated
    default void process(@NotNull P packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        throw new UnsupportedOperationException("Use response(Packet<?> packet) instead.");
    }

    /**
     * Useful for multiple listeners when only one will answer.
     *
     * @param packet    Processing packet (in)
     * @param proxy     Registered proxy
     * @param direction The direction of the incoming packet.
     * @return true if this handler can response to this packet.
     */
    default boolean answer(@NotNull P packet, @NotNull Proxy proxy, @NotNull Direction direction) {
        return true;
    }

    @NotNull
    CompletableFuture<A> response(@NotNull P packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction);
}

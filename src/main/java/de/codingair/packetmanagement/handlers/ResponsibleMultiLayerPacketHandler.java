package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ResponsibleMultiLayerPacketHandler<P extends RequestPacket<A>, A extends ResponsePacket> extends ResponsiblePacketHandler<P, A> {
    @NotNull
    CompletableFuture<A> response(@NotNull P packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) throws Escalation;
}
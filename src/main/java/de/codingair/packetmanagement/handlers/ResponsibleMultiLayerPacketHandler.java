package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ResponsibleMultiLayerPacketHandler<P extends RequestPacket<?, ?>, A extends ResponsePacket> extends ResponsiblePacketHandler<P, A> {
    @NotNull
    CompletableFuture<A> response(P packet) throws Escalation;
}
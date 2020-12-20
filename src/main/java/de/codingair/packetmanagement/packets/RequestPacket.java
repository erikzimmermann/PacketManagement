package de.codingair.packetmanagement.packets;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;

import java.util.concurrent.CompletableFuture;

public interface RequestPacket<R extends ResponsiblePacketHandler<? extends RequestPacket<?, ?>, ? extends Packet<?>>, A extends ResponsePacket> extends Packet<R>, AssignedPacket {
    default CompletableFuture<A> buildFuture() {
        return new CompletableFuture<>();
    }
}

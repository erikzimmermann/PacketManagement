package de.codingair.packetmanagement.packets;

import java.util.concurrent.CompletableFuture;

public interface RequestPacket<A extends ResponsePacket> extends AssignedPacket {
    default CompletableFuture<A> buildFuture() {
        return new CompletableFuture<>();
    }
}

package de.codingair.packetmanagement.packets;

import java.util.concurrent.CompletableFuture;

/**
 * A RequestPacket will be used to ask for some data on the receiving system. There, you have to register corresponding ResponsiblePacketHandlers to create a response accordingly.
 *
 * @param <A> We mark our ResponsePacket here to get some structure in.
 */
public interface RequestPacket<A extends ResponsePacket> extends AssignedPacket {
    default CompletableFuture<A> buildFuture() {
        return new CompletableFuture<>();
    }
}

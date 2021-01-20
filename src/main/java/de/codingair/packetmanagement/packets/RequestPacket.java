package de.codingair.packetmanagement.packets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * A RequestPacket will be used to ask for some data on the receiving system. There, you have to register corresponding ResponsiblePacketHandlers to create a response accordingly.
 *
 * @param <A> We mark our ResponsePacket here to get some structure in.
 */
public interface RequestPacket<A extends ResponsePacket> extends AssignedPacket {
    default CompletableFuture<A> buildFuture() {
        return new CompletableFuture<>();
    }

    /**
     * Used for lazy forwarding when no future is expected.
     *
     * @return A nested Packet
     */
    default Packet noFuture() {
        return new IgnoreFuture(this);
    }

    /**
     * Used for RequestPackets that need multiple results from different servers in a single channel.
     *
     * @param results Maximum of needed completed result.
     * @param merger  BiFunction to merge all completed results to one result.
     * @return A nested RequestPacket
     */
    default RequestPacket<A> mergeFuture(int results, BiFunction<A, A, A> merger) {
        return new MergeFuture<>(this, results, merger);
    }
}

package de.codingair.packetmanagement.exceptions;

import de.codingair.packetmanagement.utils.ObjectMerger;

public class TimeOutException extends PacketException {
    private ObjectMerger<?> merger;

    public TimeOutException(String message) {
        super(message);
    }

    public TimeOutException(String message, ObjectMerger<?> merger) {
        super(message);
        this.merger = merger;
    }

    /**
     * Returns the result of the merge which was used  if the requested packet had the MergeFuture tag.
     *
     * @return Merges all received CompletableFutures of this packet or the default when no future is available.
     */
    public <A> A getMerged(A def) {
        return merger == null ? def : ((ObjectMerger<A>) merger).complete(def);
    }
}

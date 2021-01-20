package de.codingair.packetmanagement.packets;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MergeFuture<R extends ResponsePacket> implements RequestPacket<R> {
    private final Packet packet;
    private final int results;
    private final BiFunction<R, R, R> merger;

    MergeFuture(@NotNull Packet packet, int results, BiFunction<R, R, R> merger) {
        this.packet = packet;
        this.results = results;
        this.merger = merger;
    }

    public Packet getPacket() {
        return packet;
    }

    public int getResults() {
        return results;
    }

    public BiFunction<R, R, R> getMerger() {
        return merger;
    }

    @Override
    public void write(DataOutputStream out) {
        throw new IllegalStateException("IgnoreFuture packets are not supposed to write data!");
    }

    @Override
    public void read(DataInputStream in) {
        throw new IllegalStateException("IgnoreFuture packets are not supposed to read data!");
    }
}

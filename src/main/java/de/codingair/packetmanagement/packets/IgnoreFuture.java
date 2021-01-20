package de.codingair.packetmanagement.packets;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public final class IgnoreFuture implements Packet {
    private final Packet packet;

    IgnoreFuture(@NotNull Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
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

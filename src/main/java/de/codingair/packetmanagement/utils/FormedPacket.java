package de.codingair.packetmanagement.utils;

import de.codingair.packetmanagement.packets.Packet;

import java.util.UUID;

public class FormedPacket {
    private final Packet packet;
    private final boolean hasFuture;
    private final UUID futureId;

    public FormedPacket(Packet packet, boolean hasFuture, UUID futureId) {
        this.packet = packet;
        this.hasFuture = hasFuture;
        this.futureId = futureId;
    }

    public Packet getPacket() {
        return packet;
    }

    public boolean hasFuture() {
        return hasFuture;
    }

    public UUID getFutureId() {
        return futureId;
    }
}

package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.packets.Packet;

public interface PacketHandler<P extends Packet<?>> {
    void process(P packet);
}

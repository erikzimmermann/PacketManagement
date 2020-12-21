package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.Proxy;

public interface PacketHandler<P extends Packet> {
    void process(P packet, Proxy proxy);
}

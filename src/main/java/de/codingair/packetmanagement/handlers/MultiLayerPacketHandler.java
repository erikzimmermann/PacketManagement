package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.Proxy;

public interface MultiLayerPacketHandler<P extends Packet> extends PacketHandler<P> {
    void process(P packet, Proxy proxy) throws Escalation;
}

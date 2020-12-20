package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.packets.Packet;

public interface MultiLayerPacketHandler<P extends Packet<?>> extends PacketHandler<P> {
    void process(P packet) throws Escalation;
}

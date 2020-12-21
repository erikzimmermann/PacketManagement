package de.codingair.packetmanagement.test.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.utils.Proxy;

public class SimplePacketHandler implements PacketHandler<SimplePacket> {
    @Override
    public void process(SimplePacket packet, Proxy proxy) {
    }
}

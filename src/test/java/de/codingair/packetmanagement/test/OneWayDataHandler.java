package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.test.handlers.NamePacketHandler;
import de.codingair.packetmanagement.test.handlers.RespondingNamePacketHandler;
import de.codingair.packetmanagement.test.handlers.SimplePacketHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.TestProxy;
import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;

public class OneWayDataHandler extends OneWaySingleConnectionDataHandler {
    public OneWaySingleConnectionDataHandler other;

    public OneWayDataHandler() {
        super("test", new TestProxy());
    }

    @Override
    protected void registering() {
        registerPacket(SimplePacket.class, SimplePacketHandler.class);
        registerPacket(NameRequestPacket.class, NamePacketHandler.class);
        registerPacket(MultiLayerNameRequestPacket.class, RespondingNamePacketHandler.class);
    }

    @Override
    protected void send(byte[] data) {
        other.receive(data);
    }
}

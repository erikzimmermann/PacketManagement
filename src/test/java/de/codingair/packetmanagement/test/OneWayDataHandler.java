package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.TestProxy;
import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;

import java.io.IOException;

public class OneWayDataHandler extends OneWaySingleConnectionDataHandler {
    public OneWaySingleConnectionDataHandler other;

    public OneWayDataHandler() {
        super("test", new TestProxy());
    }

    @Override
    protected void registering() {
        registerPacket(SimplePacket.class);
        registerPacket(NameRequestPacket.class);
        registerPacket(MultiLayerNameRequestPacket.class);
    }

    @Override
    protected void send(byte[] data) {
        try {
            other.receive(data);
        } catch(IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

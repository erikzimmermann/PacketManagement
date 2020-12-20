package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.TestProxy;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;

import java.io.IOException;

public class TestDataHandler extends OneWaySingleConnectionDataHandler {
    public TestDataHandler() {
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
            receive(data);
        } catch(IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return true;
    }
}

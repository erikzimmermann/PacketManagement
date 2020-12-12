package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.SingleChannelDataHandler;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.StringPacket;

import java.io.IOException;

public class TestDataHandler extends SingleChannelDataHandler {
    public TestDataHandler() {
        super("test");
    }

    @Override
    protected void registering() {
        registerPacket(StringPacket.class);
        registerPacket(NameRequestPacket.class);
    }

    @Override
    protected void send(byte[] data) {
        try {
            receive(data);
        } catch(IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

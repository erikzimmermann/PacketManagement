package de.codingair.packetmanagement.test.datahandlers;

import de.codingair.packetmanagement.test.packethandlers.NamePacketHandler;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.proxies.TestProxy;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.variants.gson.SimpleGsonDataHandler;

public class TestGsonDataHandler extends SimpleGsonDataHandler {
    public TestGsonDataHandler() {
        super("Test", new TestProxy());
    }

    @Override
    protected void registering() {
        registerHandler(NameRequestPacket.class, new NamePacketHandler());
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return true;
    }

    @Override
    protected void send(String json) {
        receive(json);
    }

}

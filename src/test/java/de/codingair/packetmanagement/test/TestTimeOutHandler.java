package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.TestProxy;
import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;

public class TestTimeOutHandler extends OneWaySingleConnectionDataHandler {
    public TestTimeOutHandler() {
        super("test", new TestProxy());
        timeOut = 10;
    }

    @Override
    protected void registering() {
        registerPacket(SimplePacket.class);
        registerPacket(NameRequestPacket.class);
        registerPacket(MultiLayerNameRequestPacket.class);
    }

    @Override
    protected void send(byte[] data) {
        //ignore -> triggering timeout
    }
}

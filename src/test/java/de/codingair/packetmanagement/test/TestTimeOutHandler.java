package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.SingleConnectionDataHandler;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.StringPacket;

public class TestTimeOutHandler extends SingleConnectionDataHandler {
    public TestTimeOutHandler() {
        super("test", new TestProxy());
        timeOut = 10;
    }

    @Override
    protected void send(byte[] data) {
        //ignore -> triggering timeout
    }

    @Override
    protected void registering() {
        registerPacket(StringPacket.class);
        registerPacket(NameRequestPacket.class);
    }
}

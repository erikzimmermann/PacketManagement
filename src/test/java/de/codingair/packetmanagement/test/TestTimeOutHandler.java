package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.SingleChannelDataHandler;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.StringPacket;

public class TestTimeOutHandler extends SingleChannelDataHandler {
    public TestTimeOutHandler() {
        super("test");
        timeOut = 10;
    }

    @Override
    protected void send(byte[] data) {
        //ignore triggering timeout
    }

    @Override
    protected void registering() {
        registerPacket(StringPacket.class);
        registerPacket(NameRequestPacket.class);
    }
}

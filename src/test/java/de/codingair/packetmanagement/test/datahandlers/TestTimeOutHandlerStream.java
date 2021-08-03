package de.codingair.packetmanagement.test.datahandlers;

import de.codingair.packetmanagement.test.packethandlers.NamePacketHandler;
import de.codingair.packetmanagement.test.packethandlers.RespondingNamePacketHandler;
import de.codingair.packetmanagement.test.packethandlers.SimplePacketHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.TestProxy;
import de.codingair.packetmanagement.variants.bytestream.OneWaySingleConnectionStreamDataHandler;

public class TestTimeOutHandlerStream extends OneWaySingleConnectionStreamDataHandler {
    public TestTimeOutHandlerStream() {
        super("test", new TestProxy());
        timeOut = 10;
    }

    @Override
    protected void registering() {
        registerPacket(SimplePacket.class, new SimplePacketHandler());
        registerPacket(NameRequestPacket.class, new NamePacketHandler());
        registerPacket(MultiLayerNameRequestPacket.class, new RespondingNamePacketHandler());
    }

    @Override
    protected void send(byte[] data) {
        //ignore -> triggering timeout
    }
}

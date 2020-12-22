package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.test.handlers.NamePacketHandler;
import de.codingair.packetmanagement.test.handlers.RespondingNamePacketHandler;
import de.codingair.packetmanagement.test.handlers.SimplePacketHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;
import de.codingair.packetmanagement.variants.SingleConnectionDataHandler;

public class MultiLayerHelper extends OneWaySingleConnectionDataHandler {
    private final Direction instance;
    public SingleConnectionDataHandler other;

    public MultiLayerHelper(Direction instance, Proxy proxy) {
        super("test", proxy);
        this.instance = instance;
    }

    @Override
    protected void registering() {
        registerPacket(SimplePacket.class, new SimplePacketHandler());
        registerPacket(NameRequestPacket.class, new NamePacketHandler());
        registerPacket(MultiLayerNameRequestPacket.class, new RespondingNamePacketHandler());
    }

    @Override
    protected void send(byte[] data) {
        other.receive(data, instance);
    }
}

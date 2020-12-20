package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;
import de.codingair.packetmanagement.variants.SingleConnectionDataHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;

import java.io.IOException;

public class MultiLayerHelper extends OneWaySingleConnectionDataHandler {
    private final Direction instance;
    public SingleConnectionDataHandler other;

    public MultiLayerHelper(Direction instance, Proxy proxy) {
        super("test", proxy);
        this.instance = instance;
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
            other.receive(data, instance);
        } catch(IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

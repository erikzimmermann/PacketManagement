package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.test.handlers.ForwardingNamePacketHandler;
import de.codingair.packetmanagement.test.handlers.NamePacketHandler;
import de.codingair.packetmanagement.test.handlers.RespondingNamePacketHandler;
import de.codingair.packetmanagement.test.handlers.SimplePacketHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.MultiLayerProxy;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;
import de.codingair.packetmanagement.variants.SingleConnectionDataHandler;
import org.jetbrains.annotations.NotNull;

public class TestMultiLayerDataHandler extends SingleConnectionDataHandler {
    public OneWaySingleConnectionDataHandler first, second;

    public TestMultiLayerDataHandler() {
        super("test", new MultiLayerProxy());
    }

    @Override
    protected void send(@NotNull byte[] data, @NotNull Direction direction) {
        if(direction == Direction.DOWN) first.receive(data);
        else second.receive(data);
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return direction == Direction.DOWN && first != null || direction == Direction.UP && second != null;
    }

    @Override
    protected void registering() {
        registerPacket(SimplePacket.class, new SimplePacketHandler());
        registerPacket(NameRequestPacket.class, new NamePacketHandler());
        registerPacket(MultiLayerNameRequestPacket.class, new RespondingNamePacketHandler());
    }
}

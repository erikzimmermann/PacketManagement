package de.codingair.packetmanagement.test.datahandlers;

import de.codingair.packetmanagement.test.packethandlers.ForwardingNamePacketHandler;
import de.codingair.packetmanagement.test.packethandlers.NamePacketHandler;
import de.codingair.packetmanagement.test.packethandlers.SimplePacketHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.MultiLayerProxy;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.variants.bytestream.OneWaySingleConnectionStreamDataHandler;
import de.codingair.packetmanagement.variants.bytestream.SingleConnectionStreamDataHandler;
import org.jetbrains.annotations.NotNull;

public class TestMultiLayerStreamDataHandler extends SingleConnectionStreamDataHandler {
    public OneWaySingleConnectionStreamDataHandler first, second;

    public TestMultiLayerStreamDataHandler() {
        super("test", new MultiLayerProxy());
    }

    @Override
    protected void send(@NotNull byte[] data, @NotNull Direction direction) {
        if (direction == Direction.DOWN) first.receive(data);
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
        registerPacket(MultiLayerNameRequestPacket.class, new ForwardingNamePacketHandler());
    }
}

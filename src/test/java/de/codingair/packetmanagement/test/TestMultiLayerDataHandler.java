package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.variants.OneWaySingleConnectionDataHandler;
import de.codingair.packetmanagement.variants.SingleConnectionDataHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.proxies.MultiLayerProxy;
import de.codingair.packetmanagement.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TestMultiLayerDataHandler extends SingleConnectionDataHandler {
    public OneWaySingleConnectionDataHandler first, second;

    public TestMultiLayerDataHandler() {
        super("test", new MultiLayerProxy());
    }

    @Override
    protected void send(@NotNull byte[] data, @NotNull Direction direction) {
        if(direction == Direction.DOWN) {
            try {
                first.receive(data);
            } catch(IOException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                second.receive(data);
            } catch(IOException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return direction == Direction.DOWN && first != null || direction == Direction.UP && second != null;
    }

    @Override
    protected void registering() {
        registerPacket(SimplePacket.class);
        registerPacket(NameRequestPacket.class);
        registerPacket(MultiLayerNameRequestPacket.class);
    }
}

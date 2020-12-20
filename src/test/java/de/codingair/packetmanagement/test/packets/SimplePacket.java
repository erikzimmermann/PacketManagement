package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.test.handlers.SimplePacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SimplePacket implements Packet<SimplePacketHandler> {
    @Override
    public void write(DataOutputStream out) throws IOException {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    }

    @Override
    public @NotNull SimplePacketHandler getHandler(Proxy proxy) {
        return new SimplePacketHandler();
    }
}

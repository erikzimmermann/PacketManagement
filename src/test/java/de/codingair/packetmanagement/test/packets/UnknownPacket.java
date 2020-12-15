package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.PacketHandler;
import de.codingair.packetmanagement.packets.Proxy;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnknownPacket implements Packet {
    @Override
    public void write(DataOutputStream out) throws IOException {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    }

    @Override
    public @NotNull PacketHandler<?> getHandler(Proxy proxy) {
        return null;
    }
}

package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.Proxy;
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

    @NotNull
    @Override
    public PacketHandler<?> getHandler(Proxy proxy) {
        return null;
    }
}

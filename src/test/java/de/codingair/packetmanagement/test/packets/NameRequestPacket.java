package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.packets.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NameRequestPacket implements RequestPacket<NamePacketHandler, StringPacket> {
    private int id;

    public NameRequestPacket() {
    }

    public NameRequestPacket(int id) {
        this.id = id;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.id);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.id = in.readInt();
    }

    @Override
    public NamePacketHandler getHandler() {
        return new NamePacketHandler();
    }

    public int id() {
        return id;
    }
}

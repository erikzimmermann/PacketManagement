package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.StringPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MultiLayerNameRequestPacket implements RequestPacket<StringPacket> {
    private int id;

    public MultiLayerNameRequestPacket() {
    }

    public MultiLayerNameRequestPacket(int id) {
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

    public int id() {
        return id;
    }
}

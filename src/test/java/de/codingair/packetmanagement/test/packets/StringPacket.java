package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.packets.ResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringPacket implements ResponsePacket {
    private String name;

    public StringPacket() {
    }

    public StringPacket(String name) {
        this.name = name;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
    }

    public String name() {
        return name;
    }
}

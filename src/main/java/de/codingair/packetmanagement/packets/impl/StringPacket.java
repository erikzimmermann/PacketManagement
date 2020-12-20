package de.codingair.packetmanagement.packets.impl;

import de.codingair.packetmanagement.packets.ResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringPacket implements ResponsePacket {
    private String a;

    public StringPacket() {
    }

    public StringPacket(String a) {
        this.a = a;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.a);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.a = in.readUTF();
    }

    public String a() {
        return a;
    }
}

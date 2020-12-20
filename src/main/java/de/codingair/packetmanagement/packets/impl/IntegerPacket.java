package de.codingair.packetmanagement.packets.impl;

import de.codingair.packetmanagement.packets.ResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerPacket implements ResponsePacket {
    private int a;

    public IntegerPacket() {
    }

    public IntegerPacket(int a) {
        this.a = a;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.a);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.a = in.readInt();
    }

    public int a() {
        return a;
    }
}

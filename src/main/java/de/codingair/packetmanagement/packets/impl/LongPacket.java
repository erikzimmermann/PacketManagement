package de.codingair.packetmanagement.packets.impl;

import de.codingair.packetmanagement.packets.ResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongPacket implements ResponsePacket {
    private long a;

    public LongPacket() {
    }

    public LongPacket(long a) {
        this.a = a;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(this.a);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.a = in.readLong();
    }

    public long a() {
        return a;
    }
}

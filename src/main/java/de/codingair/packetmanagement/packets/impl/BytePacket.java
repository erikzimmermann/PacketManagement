package de.codingair.packetmanagement.packets.impl;

import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.utils.ByteMask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BytePacket implements ResponsePacket {
    private final ByteMask mask;

    public BytePacket() {
        this.mask = new ByteMask();
    }

    public BytePacket(boolean a) {
        this.mask = new ByteMask((byte) (a ? 1 : 0));
    }

    public BytePacket(byte a) {
        this.mask = new ByteMask(a);
    }

    public BytePacket(ByteMask mask) {
        this.mask = mask;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        this.mask.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.mask.read(in);
    }

    public ByteMask getMask() {
        return this.mask;
    }

    public byte getByte() {
        return this.mask.getByte();
    }

    public boolean getBoolean() {
        return this.mask.getBit(0);
    }
}

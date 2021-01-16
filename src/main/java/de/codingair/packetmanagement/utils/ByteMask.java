package de.codingair.packetmanagement.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteMask implements Serializable {
    private byte b;

    public ByteMask() {
        b = 0;
    }

    public ByteMask(byte b) {
        this.b = b;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(this.b);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.b = in.readByte();
    }

    public boolean getBit(int index) {
        return ((b >> index) & 1) == 1;
    }

    public boolean setBit(int index, boolean bit) {
        boolean before = getBit(index);

        if (bit) set(index);
        else clear(index);

        return before;
    }

    private void set(int index) {
        this.b |= (1 << index);
    }

    private void clear(int index) {
        this.b &= ~(1 << index);
    }

    public byte getByte() {
        return b;
    }
}

package de.codingair.packetmanagement.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteMask implements Serializable{
    private byte b;

    public ByteMask() {
        b = 0;
    }

    public ByteMask(byte b) {
        this.b = b;
    }

    public boolean getBit(int index) {
        return ((b >> index) & 1) == 1;
    }

    public boolean updateBit(int index, boolean bit) {
        boolean before = getBit(index);
        setBit(index, bit);
        return before;
    }

    public void setBit(int index, boolean bit) {
        if(bit) this.b |= (1 << index);
        else this.b &= ~(1 << index);
    }

    public void setBit(int index) {
        setBit(index, true);
    }

    public void clearBit(int index) {
        setBit(index, false);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(this.b);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.b = in.readByte();
    }
}

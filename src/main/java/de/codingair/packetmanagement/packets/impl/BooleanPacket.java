package de.codingair.packetmanagement.packets.impl;

import de.codingair.packetmanagement.utils.ByteMask;

public class BooleanPacket extends BytePacket {
    public BooleanPacket() {
        super();
    }

    public BooleanPacket(boolean a) {
        super(new ByteMask((byte) (a ? 1 : 0)));
    }

    public boolean getBoolean() {
        return this.mask.getBit(0);
    }
}

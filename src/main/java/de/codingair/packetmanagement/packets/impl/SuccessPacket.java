package de.codingair.packetmanagement.packets.impl;

public class SuccessPacket extends BooleanPacket {
    public SuccessPacket() {
        super();
    }

    public SuccessPacket(boolean success) {
        super(success);
    }
}

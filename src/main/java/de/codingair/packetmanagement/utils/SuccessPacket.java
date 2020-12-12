package de.codingair.packetmanagement.utils;

import de.codingair.packetmanagement.packets.ResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SuccessPacket implements ResponsePacket {
    @Override
    public void write(DataOutputStream out) throws IOException {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    }
}

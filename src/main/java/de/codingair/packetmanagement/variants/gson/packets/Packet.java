package de.codingair.packetmanagement.variants.gson.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet extends de.codingair.packetmanagement.packets.Packet {
    @Override
    default void write(DataOutputStream dataOutputStream) throws IOException {
        throw new IllegalStateException("Not supported for gson data handler");
    }

    @Override
    default void read(DataInputStream dataInputStream) throws IOException {
        throw new IllegalStateException("Not supported for gson data handler");
    }
}

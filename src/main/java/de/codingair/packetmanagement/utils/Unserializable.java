package de.codingair.packetmanagement.utils;

import de.codingair.packetmanagement.exceptions.PacketNotSerializableException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Unserializable extends Serializable {

    @Override
    default void write(DataOutputStream out) throws IOException {
        throw new PacketNotSerializableException(getClass());
    }

    @Override
    default void read(DataInputStream in) throws IOException {
        throw new PacketNotSerializableException(getClass());
    }
}

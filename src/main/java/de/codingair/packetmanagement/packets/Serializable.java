package de.codingair.packetmanagement.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Serializable {
    void write(DataOutputStream out) throws IOException;

    void read(DataInputStream in) throws IOException;
}

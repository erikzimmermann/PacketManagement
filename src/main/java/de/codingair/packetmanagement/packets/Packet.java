package de.codingair.packetmanagement.packets;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet<P extends PacketHandler<?>> {
    void write(DataOutputStream out) throws IOException;

    void read(DataInputStream in) throws IOException;

    /**
     *
     * @param proxy The registered proxy server
     * @return The corresponding packet handler for that proxy.
     */
    @NotNull
    P getHandler(Proxy proxy);
}

package de.codingair.packetmanagement.packets;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;

public interface ResponsePacket extends Packet<PacketHandler<?>>, AssignedPacket {
    @Override
    @Deprecated
    default @NotNull PacketHandler<?> getHandler(Proxy proxy) {
        throw new UnsupportedOperationException("A ResponsePacket does not have a PacketHandler.");
    }
}

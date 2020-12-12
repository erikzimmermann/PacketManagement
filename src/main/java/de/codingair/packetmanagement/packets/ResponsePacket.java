package de.codingair.packetmanagement.packets;

import org.jetbrains.annotations.NotNull;

public interface ResponsePacket extends Packet<PacketHandler<?>>, AssignedPacket {
    @Override
    default @NotNull PacketHandler<?> getHandler() {
        throw new UnsupportedOperationException("A ResponsePacket does not have a PacketHandler.");
    }
}

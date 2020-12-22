package de.codingair.packetmanagement.utils;

import de.codingair.packetmanagement.packets.ResponsePacket;

public interface PacketSupplier<R extends ResponsePacket> {
    R exceptional(Throwable err);
}

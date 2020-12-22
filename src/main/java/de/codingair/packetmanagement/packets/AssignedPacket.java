package de.codingair.packetmanagement.packets;

/**
 * An AssignedPacket signalizes the DataHandler that this packet implementation must be marked with an UUID.
 * Useful for request/response packets.
 */
public interface AssignedPacket extends Packet {
}

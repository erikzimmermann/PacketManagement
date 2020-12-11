package de.codingair.packetmanagement.packets;

public interface PacketHandler<P extends Packet<?>> {

    void process(P packet);

}

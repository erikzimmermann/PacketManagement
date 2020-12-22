package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MultiLayerPacketHandler<P extends Packet> extends PacketHandler<P> {
    void process(@NotNull P packet, @NotNull Proxy proxy, @Nullable Object connection) throws Escalation;
}

package de.codingair.packetmanagement.handlers;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PacketHandler<P extends Packet> {
    void process(@NotNull P packet, @NotNull Proxy proxy, @Nullable Object connection);
}

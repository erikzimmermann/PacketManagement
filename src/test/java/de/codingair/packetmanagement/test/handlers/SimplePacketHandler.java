package de.codingair.packetmanagement.test.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimplePacketHandler implements PacketHandler<SimplePacket> {
    @Override
    public void process(@NotNull SimplePacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
    }
}

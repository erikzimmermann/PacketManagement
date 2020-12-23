package de.codingair.packetmanagement.test.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class NamePacketHandler implements ResponsiblePacketHandler<NameRequestPacket, StringPacket> {
    @Override
    public @NotNull CompletableFuture<StringPacket> response(@NotNull NameRequestPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if(packet.id() == 0) return CompletableFuture.completedFuture(new StringPacket("CodingAir"));
        else return CompletableFuture.completedFuture(new StringPacket("UNKNOWN"));
    }
}

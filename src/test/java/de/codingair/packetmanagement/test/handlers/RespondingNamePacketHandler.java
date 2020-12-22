package de.codingair.packetmanagement.test.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RespondingNamePacketHandler implements ResponsibleMultiLayerPacketHandler<MultiLayerNameRequestPacket, StringPacket> {
    @Override
    public @NotNull CompletableFuture<StringPacket> response(@NotNull MultiLayerNameRequestPacket packet, @NotNull Proxy proxy, @Nullable Object connection) throws Escalation {
        if(packet.id() == 0) return CompletableFuture.completedFuture(new StringPacket("CodingAir"));
        return CompletableFuture.completedFuture(new StringPacket("UNKNOWN"));
    }
}

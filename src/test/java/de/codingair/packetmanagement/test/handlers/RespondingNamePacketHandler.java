package de.codingair.packetmanagement.test.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RespondingNamePacketHandler implements ResponsibleMultiLayerPacketHandler<MultiLayerNameRequestPacket, StringPacket> {
    @Override
    public @NotNull CompletableFuture<StringPacket> response(MultiLayerNameRequestPacket packet) throws Escalation {
        if(packet.id() == 0) return CompletableFuture.completedFuture(new StringPacket("CodingAir"));
        return CompletableFuture.completedFuture(new StringPacket("UNKNOWN"));
    }
}

package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.packets.ResponsiblePacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NamePacketHandler implements ResponsiblePacketHandler<NameRequestPacket, StringPacket> {
    @Override
    public @NotNull CompletableFuture<StringPacket> response(NameRequestPacket packet) {
        if(packet.id() == 0) return CompletableFuture.completedFuture(new StringPacket("CodingAir"));
        else return CompletableFuture.completedFuture(new StringPacket("UNKNOWN"));
    }
}

package de.codingair.packetmanagement.test.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ForwardingNamePacketHandler implements ResponsibleMultiLayerPacketHandler<MultiLayerNameRequestPacket, StringPacket> {
    @Override
    public @NotNull CompletableFuture<StringPacket> response(MultiLayerNameRequestPacket packet, Proxy proxy) throws Escalation {
        if(packet.id() == 0) throw new Escalation(this, Direction.UP, packet, new StringPacket("UNKNOWN"));
        return CompletableFuture.completedFuture(new StringPacket("UNKNOWN"));
    }
}

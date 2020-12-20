package de.codingair.packetmanagement.test.packets;

import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import de.codingair.packetmanagement.test.handlers.ForwardingNamePacketHandler;
import de.codingair.packetmanagement.test.handlers.RespondingNamePacketHandler;
import de.codingair.packetmanagement.test.proxies.MultiLayerProxy;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MultiLayerNameRequestPacket implements RequestPacket<ResponsibleMultiLayerPacketHandler<MultiLayerNameRequestPacket, StringPacket>, StringPacket> {
    private int id;

    public MultiLayerNameRequestPacket() {
    }

    public MultiLayerNameRequestPacket(int id) {
        this.id = id;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.id);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.id = in.readInt();
    }

    @Override
    public @NotNull ResponsibleMultiLayerPacketHandler<MultiLayerNameRequestPacket, StringPacket> getHandler(Proxy proxy) {
        if(proxy instanceof MultiLayerProxy) return new ForwardingNamePacketHandler();
        else return new RespondingNamePacketHandler();
    }

    public int id() {
        return id;
    }
}

package de.codingair.packetmanagement.packets.impl;

import de.codingair.packetmanagement.packets.ResponsePacket;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringPacket implements ResponsePacket {
    private byte options;
    private String a;

    public StringPacket() {
    }

    public StringPacket(@Nullable String a) {
        options = (byte) (a == null ? 0 : 1);
        this.a = a;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(this.options);
        if(this.a != null) out.writeUTF(this.a);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.options = in.readByte();
        if((this.options & 1) == 1) this.a = in.readUTF();
    }

    public String a() {
        return a;
    }
}

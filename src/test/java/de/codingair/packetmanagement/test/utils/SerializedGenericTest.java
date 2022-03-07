package de.codingair.packetmanagement.test.utils;

import de.codingair.packetmanagement.utils.SerializedGeneric;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;

class SerializedGenericTest {

    @Test
    void testFloat() throws IOException, ClassNotFoundException {
        SerializedGeneric g = new SerializedGeneric(0.5F);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        assert Objects.equals(0.5F, g.getObject());
    }

    @Test
    void testByte() throws IOException, ClassNotFoundException {
        SerializedGeneric g = new SerializedGeneric((byte) 1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        assert Objects.equals((byte) 1, g.getObject());
    }

    @Test
    void testInt() throws IOException, ClassNotFoundException {
        SerializedGeneric g = new SerializedGeneric(1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        assert Objects.equals(1, g.getObject());
    }

    @Test
    void testNotInt() throws IOException, ClassNotFoundException {
        SerializedGeneric g = new SerializedGeneric(1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        assert !Objects.equals((byte) 1, g.getObject());
    }
}
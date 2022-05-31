package de.codingair.packetmanagement.test.utils;

import de.codingair.packetmanagement.utils.SerializedGeneric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Objects;

class SerializedGenericTest {

    @Test
    void testFloat() throws IOException {
        SerializedGeneric g = new SerializedGeneric(0.5F);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        Assertions.assertEquals(0.5F, (float) g.getObject());
    }

    @Test
    void testByte() throws IOException {
        SerializedGeneric g = new SerializedGeneric((byte) -12);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        Assertions.assertEquals((byte) -12, (byte) g.getObject());
    }

    @Test
    void testInt() throws IOException {
        SerializedGeneric g = new SerializedGeneric(2975);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        Assertions.assertEquals(2975, (int) g.getObject());
    }

    @Test
    void testGeneral() throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        byte test = (byte) -12;

        ByteArrayOutputStream nestedBaos = new ByteArrayOutputStream();
        DataOutputStream nestedOut = new DataOutputStream(nestedBaos);
        nestedOut.writeByte(test);

        byte[] object = nestedBaos.toByteArray();
        byte[] data = Base64.getEncoder().encode(object);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        String encodedObject = new String(data, charset);
        out.writeUTF(encodedObject);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        String resultEncodedObject = in.readUTF();

        Assertions.assertEquals(encodedObject, resultEncodedObject);

        byte[] resultObject = Base64.getDecoder().decode(resultEncodedObject.getBytes(charset));

        Assertions.assertArrayEquals(object, resultObject);

        DataInputStream nestedIn = new DataInputStream(new ByteArrayInputStream(resultObject));
        byte result = nestedIn.readByte();

        Assertions.assertEquals(test, result);
    }

    @Test
    void testString() throws IOException {
        SerializedGeneric g = new SerializedGeneric("v");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        Assertions.assertEquals("v", g.getObject());
    }

    @Test
    void testNotInt() throws IOException {
        SerializedGeneric g = new SerializedGeneric(1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        assert !Objects.equals((byte) 1, g.getObject());
    }

    @Test
    void testComplex() throws IOException {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        map.put("v", 2975);
        map.put("type", "STONE");
        map.put("amount", 64);

        SerializedGeneric g = new SerializedGeneric(map);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        SerializedGeneric serialized = new SerializedGeneric();
        serialized.read(in);

        Assertions.assertDoesNotThrow(() -> {
            LinkedHashMap<Object, Object> result = serialized.getObject();
            Assertions.assertEquals(map, result);
        });
    }
}
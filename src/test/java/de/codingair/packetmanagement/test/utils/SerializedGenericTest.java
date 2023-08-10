package de.codingair.packetmanagement.test.utils;

import de.codingair.packetmanagement.utils.SerializedGeneric;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Objects;

class SerializedGenericTest {

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

    @NotNull
    private static SerializedGeneric performWriteRead(SerializedGeneric g) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        g.write(out);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        g = new SerializedGeneric();
        g.read(in);

        return g;
    }

    @Test
    void testFloat() throws IOException {
        SerializedGeneric g = new SerializedGeneric(0.5F);

        g = performWriteRead(g);

        Assertions.assertEquals(0.5F, (float) g.getObject());
    }

    @Test
    void testByte() throws IOException {
        SerializedGeneric g = new SerializedGeneric((byte) -12);

        g = performWriteRead(g);

        Assertions.assertEquals((byte) -12, (byte) g.getObject());
    }

    @Test
    void testInt() throws IOException {
        SerializedGeneric g = new SerializedGeneric(2975);

        g = performWriteRead(g);

        Assertions.assertEquals(2975, (int) g.getObject());
    }

    @Test
    void testString() throws IOException {
        SerializedGeneric g = new SerializedGeneric("v");

        g = performWriteRead(g);

        Assertions.assertEquals("v", g.getObject());
    }

    @Test
    void testNotInt() throws IOException {
        SerializedGeneric g = new SerializedGeneric(1);

        g = performWriteRead(g);

        assert !Objects.equals((byte) 1, g.getObject());
    }

    @Test
    void testComplex() throws IOException {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        map.put("v", 2975);
        map.put("type", "STONE");
        map.put("amount", 64);

        SerializedGeneric g = new SerializedGeneric(map);

        g = performWriteRead(g);

        SerializedGeneric finalG = g;
        Assertions.assertDoesNotThrow(() -> {
            LinkedHashMap<Object, Object> result = finalG.getObject();
            Assertions.assertEquals(map, result);
        });
    }

    @Test
    void testEnum() throws IOException {
        SerializedGeneric g = new SerializedGeneric(new TestClass(TestEnum.A));

        g = performWriteRead(g);

        TestClass serialized = g.getObject();
        assert Objects.equals(TestEnum.A, serialized.getTestEnum());
    }

    private static class TestClass {
        private final TestEnum testEnum;

        public TestClass(TestEnum testEnum) {
            this.testEnum = testEnum;
        }

        public TestEnum getTestEnum() {
            return testEnum;
        }
    }

    private enum TestEnum {
        A("A"), B("B"), C("C");

        private final String name;

        TestEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
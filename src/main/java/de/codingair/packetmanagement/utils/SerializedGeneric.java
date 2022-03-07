package de.codingair.packetmanagement.utils;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

public class SerializedGeneric implements Serializable {
    private String object;

    public SerializedGeneric() {
    }

    public SerializedGeneric(@NotNull Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        Generic.write(dos, object);

        this.object = baos.toString();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.object);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.object = in.readUTF();
    }

    public byte[] getData() {
        return object.getBytes(StandardCharsets.UTF_8);
    }

    @NotNull
    public Object getObject() throws IOException {
        ByteArrayInputStream baos = new ByteArrayInputStream(getData());
        DataInputStream dis = new DataInputStream(baos);
        return Generic.read(dis);
    }

    private static class GsonWrapper {
        public final Object value;

        public GsonWrapper(Object value) {
            this.value = value;
        }
    }

    private enum Generic {
        BYTE(Byte.class, new GenericHandler<Byte>() {
            @Override
            public void handling(DataOutputStream out, Byte o) throws IOException {
                out.writeByte(o);
            }

            @Override
            public @NotNull Byte read(DataInputStream in) throws IOException {
                return in.readByte();
            }
        }),
        SHORT(Short.class, new GenericHandler<Short>() {
            @Override
            public void handling(DataOutputStream out, Short o) throws IOException {
                out.writeShort(o);
            }

            @Override
            public @NotNull Short read(DataInputStream in) throws IOException {
                return in.readShort();
            }
        }),
        INT(Integer.class, new GenericHandler<Integer>() {
            @Override
            public void handling(DataOutputStream out, Integer o) throws IOException {
                out.writeInt(o);
            }

            @Override
            public @NotNull Integer read(DataInputStream in) throws IOException {
                return in.readInt();
            }
        }),
        LONG(Long.class, new GenericHandler<Long>() {
            @Override
            public void handling(DataOutputStream out, Long o) throws IOException {
                out.writeLong(o);
            }

            @Override
            public @NotNull Long read(DataInputStream in) throws IOException {
                return in.readLong();
            }
        }),
        FLOAT(Float.class, new GenericHandler<Float>() {
            @Override
            public void handling(DataOutputStream out, Float o) throws IOException {
                out.writeFloat(o);
            }

            @Override
            public @NotNull Float read(DataInputStream in) throws IOException {
                return in.readFloat();
            }
        }),
        DOUBLE(Double.class, new GenericHandler<Double>() {
            @Override
            public void handling(DataOutputStream out, Double o) throws IOException {
                out.writeDouble(o);
            }

            @Override
            public @NotNull Double read(DataInputStream in) throws IOException {
                return in.readDouble();
            }
        }),
        BOOLEAN(Boolean.class, new GenericHandler<Boolean>() {
            @Override
            public void handling(DataOutputStream out, Boolean o) throws IOException {
                out.writeBoolean(o);
            }

            @Override
            public @NotNull Boolean read(DataInputStream in) throws IOException {
                return in.readBoolean();
            }
        }),
        STRING(String.class, new GenericHandler<String>() {
            @Override
            public void handling(DataOutputStream out, String o) throws IOException {
                out.writeUTF(o);
            }

            @Override
            public @NotNull String read(DataInputStream in) throws IOException {
                return in.readUTF();
            }
        }),
        LINKED_MAP(LinkedHashMap.class, new MapHandler<>(LinkedHashMap::new)),
        MAP(Map.class, new MapHandler<>(HashMap::new)),
        Set(Set.class, new CollectionHandler<>(HashSet::new)),
        LIST(List.class, new CollectionHandler<>(ArrayList::new)),
        GSON(null, new GenericHandler<Object>() {
            private final Gson gson = new GsonBuilder().registerTypeAdapter(GsonWrapper.class, new GsonHandler()).create();

            class GsonHandler implements JsonSerializer<Object>, JsonDeserializer<Object> {
                @Override
                public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                    JsonObject json = jsonElement.getAsJsonObject();
                    String path = json.get("class").getAsString();
                    JsonElement objData = json.get("data");

                    Class<?> c;
                    try {
                        c = Class.forName(path);
                    } catch (ClassNotFoundException e) {
                        throw new JsonParseException(e);
                    }

                    return jdc.deserialize(objData, c);
                }

                @Override
                public JsonElement serialize(Object o, Type type, JsonSerializationContext jsc) {
                    JsonObject json = new JsonObject();
                    json.addProperty("class", o.getClass().getName());
                    json.add("data", jsc.serialize(o));
                    return json;
                }
            }

            @Override
            protected void handling(DataOutputStream out, Object o) throws IOException {
                String data = gson.toJsonTree(new GsonWrapper(o)).toString();
                out.writeUTF(data);
            }

            @Override
            public @NotNull Object read(DataInputStream in) throws IOException {
                String data = in.readUTF();

                GsonWrapper json = gson.fromJson(data, GsonWrapper.class);
                return json.value;
            }
        });

        private final Class<?> generic;
        private final GenericHandler<?> handler;

        <G> Generic(@Nullable Class<G> generic, @NotNull GenericHandler<G> handler) {
            this.generic = generic;
            this.handler = handler;
        }

        private static Generic getBy(Object o) {
            for (Generic value : values()) {
                if (value.generic != null && value.generic.isInstance(o)) return value;
            }

            return Generic.GSON;
        }

        public static void write(DataOutputStream out, Object o) throws IOException {
            Generic g = getBy(o);
            out.writeByte(g.ordinal());
            g.handler.write(out, o);
        }

        @NotNull
        public static Object read(DataInputStream in) throws IOException {
            int id = in.readUnsignedByte();
            return values()[id].handler.read(in);
        }

        private static class MapHandler<M extends Map<?, ?>> extends GenericHandler<M> {
            private final Supplier<M> mapInstance;

            public MapHandler(Supplier<M> mapInstance) {
                this.mapInstance = mapInstance;
            }

            @Override
            public void handling(DataOutputStream out, M o) throws IOException {
                Map<Object, Object> map = (Map<Object, Object>) o;

                int size = map.size();

                //max unsigned short value
                if (size > 65535) throw new IllegalArgumentException("Cannot serialize maps with a size > 65.535!");

                out.writeShort(size);
                for (Map.Entry<Object, Object> e : map.entrySet()) {
                    SerializedGeneric key = new SerializedGeneric(e.getKey());
                    key.write(out);

                    SerializedGeneric value = new SerializedGeneric(e.getValue());
                    try {
                        value.write(out);
                    } catch (UnsupportedTypeException ex) {
                        throw new UnsupportedTypeException(e.getKey(), ex.o);
                    }
                }
            }

            @Override
            public @NotNull M read(DataInputStream in) throws IOException {
                Map<Object, Object> data = (Map<Object, Object>) mapInstance.get();

                int size = in.readUnsignedShort();
                for (int i = 0; i < size; i++) {
                    SerializedGeneric key = new SerializedGeneric();
                    key.read(in);

                    SerializedGeneric value = new SerializedGeneric();
                    value.read(in);

                    data.put(key.getObject(), value.getObject());
                }

                return (M) data;
            }
        }

        private static class CollectionHandler<C extends Collection> extends GenericHandler<C> {
            private final Supplier<C> collectionInstance;

            public CollectionHandler(Supplier<C> collectionInstance) {
                this.collectionInstance = collectionInstance;
            }

            @Override
            public void handling(DataOutputStream out, C o) throws IOException {
                Collection<Object> list = o;
                int size = list.size();

                //max unsigned short value
                if (size > 65535) throw new IllegalArgumentException("Cannot serialize lists with a size > 65.535!");

                out.writeShort(size);
                for (Object data : list) {
                    SerializedGeneric gen = new SerializedGeneric(data);
                    gen.write(out);
                }
            }

            @Override
            public @NotNull C read(DataInputStream in) throws IOException {
                Collection<Object> list = collectionInstance.get();

                int size = in.readUnsignedShort();
                for (int i = 0; i < size; i++) {
                    SerializedGeneric gen = new SerializedGeneric();
                    gen.read(in);
                    list.add(gen.getObject());
                }

                return (C) list;
            }
        }

        private static abstract class GenericHandler<G> {
            public void write(DataOutputStream out, Object o) throws IOException {
                handling(out, (G) o);
            }

            protected abstract void handling(DataOutputStream out, G o) throws IOException;

            @NotNull
            public abstract G read(DataInputStream in) throws IOException;
        }
    }

    public static class UnsupportedTypeException extends IllegalStateException {
        private final Object o;

        public UnsupportedTypeException(@Nullable Object key, @NotNull Object o) {
            super("Object " + o + " is not a generic! Class: " + o.getClass().getName() + (key == null ? "" : " (Key: '" + key + "')"));
            this.o = o;
        }
    }
}

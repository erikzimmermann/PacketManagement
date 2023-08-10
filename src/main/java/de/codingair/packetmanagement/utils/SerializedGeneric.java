package de.codingair.packetmanagement.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SerializedGeneric implements Serializable {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private byte[] object;

    public SerializedGeneric() {
    }

    public SerializedGeneric(@NotNull Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        Generic.write(dos, object);

        this.object = baos.toByteArray();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte[] data = Base64.getEncoder().encode(getData());
        out.writeUTF(new String(data, CHARSET));
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.object = Base64.getDecoder().decode(in.readUTF().getBytes(CHARSET));
    }

    public byte[] getData() {
        return object;
    }

    @SuppressWarnings ("unchecked")
    @NotNull
    public <T> T getObject() throws IOException {
        DataInputStream dis = asStream();
        return (T) Generic.read(dis);
    }

    @NotNull
    private DataInputStream asStream() {
        return new DataInputStream(new ByteArrayInputStream(getData()));
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

            @Override
            public Byte getDefault() {
                return 0;
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

            @Override
            public Short getDefault() {
                return 0;
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

            @Override
            public Integer getDefault() {
                return 0;
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

            @Override
            public Long getDefault() {
                return 0L;
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

            @Override
            public Float getDefault() {
                return 0F;
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

            @Override
            public Double getDefault() {
                return 0D;
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

            @Override
            public Boolean getDefault() {
                return false;
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

            @Override
            public String getDefault() {
                return null;
            }
        }),
        LINKED_MAP(LinkedHashMap.class, new MapHandler<>(LinkedHashMap::new)),
        MAP(Map.class, new MapHandler<>(HashMap::new)),
        Set(Set.class, new CollectionHandler<>(HashSet::new)),
        LIST(List.class, new CollectionHandler<>(ArrayList::new)),
        UNKNOWN(null, new GenericHandler<Object>() {

            private void fields(Class<?> c, Consumer<Field> consumer) throws IOException {
                for (Field f : c.getDeclaredFields()) {
                    int flags = f.getModifiers();
                    if (Modifier.isStatic(flags) || Modifier.isTransient(flags)) continue;

                    f.setAccessible(true);

                    try {
                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchFieldException ignored) {
                    }

                    try {
                        consumer.accept(f);
                    } catch (RuntimeException ex) {
                        if (ex.getCause() instanceof IOException) throw (IOException) ex.getCause();
                        else throw ex;
                    }
                }

                if (c.getSuperclass() != null) fields(c.getSuperclass(), consumer);
            }

            @Override
            protected void handling(DataOutputStream out, Object o) throws IOException {
                out.writeUTF(o.getClass().getName());
                fields(o.getClass(), field -> {
                    try {
                        Generic.write(out, field.get(o));
                    } catch (IOException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            public @NotNull Object read(DataInputStream in) throws IOException {
                String path = in.readUTF();

                try {
                    Class<?> c = Class.forName(path);

                    Constructor<?> con;
                    try {
                        con = c.getDeclaredConstructor();
                    } catch (NoSuchMethodException ex) {
                        con = Arrays.stream(c.getDeclaredConstructors())
                                .min(Comparator.comparingInt(Constructor::getParameterCount))
                                .orElseThrow(() -> new NoSuchMethodException("No constructor found for " + c.getName()));
                    }

                    con.setAccessible(true);

                    Class<?>[] para = con.getParameterTypes();
                    Object[] os = new Object[para.length];
                    if (para.length > 0) {
                        for (int i = 0; i < para.length; i++) {
                            os[i] = getBy(para[i]).handler.getDefault();
                        }
                    }

                    Object o = con.newInstance(os);

                    fields(o.getClass(), field -> {
                        try {
                            field.set(o, Generic.read(in));
                        } catch (IOException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    return o;
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public Object getDefault() {
                return null;
            }
        }),
        @SuppressWarnings ("rawtypes")
        ENUM(Enum.class, new GenericHandler<Enum>() {
            @Override
            protected void handling(DataOutputStream out, Enum o) throws IOException {
                out.writeUTF(o.getClass().getName());
                out.writeUTF(o.name());
            }

            @Override
            public @NotNull Enum<?> read(DataInputStream in) throws IOException {
                String path = in.readUTF();
                String name = in.readUTF();

                try {
                    Class<?> c = Class.forName(path);
                    if (c.isEnum()) {
                        //noinspection unchecked
                        return Enum.valueOf((Class<? extends Enum>) c, name);
                    } else throw new IllegalStateException("Class " + path + " is not an enum");
                } catch (ClassNotFoundException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public Enum<?> getDefault() {
                return null;
            }
        });

        private final Class<?> generic;
        private final GenericHandler<?> handler;

        <G> Generic(@Nullable Class<G> generic, @NotNull GenericHandler<G> handler) {
            this.generic = generic;
            this.handler = handler;
        }

        private static Generic getBy(Object o) {
            return getBy(o.getClass());
        }

        private static Class<?> toNonPrimitive(Class<?> c) {
            if (c == byte.class) return Byte.class;
            else if (c == short.class) return Short.class;
            else if (c == int.class) return Integer.class;
            else if (c == long.class) return Long.class;
            else if (c == float.class) return Float.class;
            else if (c == double.class) return Double.class;
            else if (c == boolean.class) return Boolean.class;
            else return c;
        }

        private static Generic getBy(Class<?> c) {
            c = toNonPrimitive(c);
            for (Generic value : values()) {
                if (value.generic != null && (value.generic == c || value.generic.isAssignableFrom(c))) return value;
            }

            return Generic.UNKNOWN;
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

        private static class MapHandler<M extends Map<Object, Object>> extends GenericHandler<M> {
            private final Supplier<M> mapInstance;

            public MapHandler(Supplier<M> mapInstance) {
                this.mapInstance = mapInstance;
            }

            @Override
            public void handling(DataOutputStream out, M map) throws IOException {
                int size = map.size();

                //max unsigned short value
                if (size > 65535) throw new IllegalArgumentException("Cannot serialize maps with a size > 65.535!");

                out.writeShort(size);
                for (Map.Entry<Object, Object> e : map.entrySet()) {
                    Generic.write(out, e.getKey());

                    try {
                        Generic.write(out, e.getValue());
                    } catch (UnsupportedTypeException ex) {
                        throw new UnsupportedTypeException(e.getKey(), ex.o);
                    }
                }
            }

            @Override
            public @NotNull M read(DataInputStream in) throws IOException {
                M data = mapInstance.get();

                int size = in.readUnsignedShort();
                for (int i = 0; i < size; i++) {
                    data.put(Generic.read(in), Generic.read(in));
                }

                return data;
            }

            @Override
            public M getDefault() {
                return mapInstance.get();
            }
        }

        private static class CollectionHandler<C extends Collection<Object>> extends GenericHandler<C> {
            private final Supplier<C> collectionInstance;

            public CollectionHandler(Supplier<C> collectionInstance) {
                this.collectionInstance = collectionInstance;
            }

            @Override
            public void handling(DataOutputStream out, C collection) throws IOException {
                int size = collection.size();

                //max unsigned short value
                if (size > 65535) throw new IllegalArgumentException("Cannot serialize lists with a size > 65.535!");

                out.writeShort(size);
                for (Object data : collection) {
                    Generic.write(out, data);
                }
            }

            @Override
            public @NotNull C read(DataInputStream in) throws IOException {
                C list = collectionInstance.get();

                int size = in.readUnsignedShort();
                for (int i = 0; i < size; i++) {
                    list.add(Generic.read(in));
                }

                return list;
            }

            @Override
            public C getDefault() {
                return collectionInstance.get();
            }
        }

        private static abstract class GenericHandler<G> {
            public void write(DataOutputStream out, Object o) throws IOException {
                //noinspection unchecked
                handling(out, (G) o);
            }

            protected abstract void handling(DataOutputStream out, G o) throws IOException;

            @NotNull
            public abstract G read(DataInputStream in) throws IOException;

            public abstract G getDefault();
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

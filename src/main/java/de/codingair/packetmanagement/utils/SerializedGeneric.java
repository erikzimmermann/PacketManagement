package de.codingair.packetmanagement.utils;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class SerializedGeneric implements Serializable {
    private Object object;

    public SerializedGeneric() {
    }

    public SerializedGeneric(@NotNull Object object) {
        this.object = object;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        Generic.write(out, this.object);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.object = Generic.read(in);
    }

    public Object getObject() {
        return object;
    }

    private enum Generic {
        BYTE(Byte.class, new GenericHandler<Byte>() {
            @Override
            public void handling(DataOutputStream out, Byte o) throws IOException {
                out.writeByte(o);
            }

            @Override
            public Byte read(DataInputStream in) throws IOException {
                return in.readByte();
            }
        }),
        SHORT(Short.class, new GenericHandler<Short>() {
            @Override
            public void handling(DataOutputStream out, Short o) throws IOException {
                out.writeShort(o);
            }

            @Override
            public Short read(DataInputStream in) throws IOException {
                return in.readShort();
            }
        }),
        INT(Integer.class, new GenericHandler<Integer>() {
            @Override
            public void handling(DataOutputStream out, Integer o) throws IOException {
                out.writeInt(o);
            }

            @Override
            public Integer read(DataInputStream in) throws IOException {
                return in.readInt();
            }
        }),
        LONG(Long.class, new GenericHandler<Long>() {
            @Override
            public void handling(DataOutputStream out, Long o) throws IOException {
                out.writeLong(o);
            }

            @Override
            public Long read(DataInputStream in) throws IOException {
                return in.readLong();
            }
        }),
        FLOAT(Float.class, new GenericHandler<Float>() {
            @Override
            public void handling(DataOutputStream out, Float o) throws IOException {
                out.writeFloat(o);
            }

            @Override
            public Float read(DataInputStream in) throws IOException {
                return in.readFloat();
            }
        }),
        DOUBLE(Double.class, new GenericHandler<Double>() {
            @Override
            public void handling(DataOutputStream out, Double o) throws IOException {
                out.writeDouble(o);
            }

            @Override
            public Double read(DataInputStream in) throws IOException {
                return in.readDouble();
            }
        }),
        BOOLEAN(Boolean.class, new GenericHandler<Boolean>() {
            @Override
            public void handling(DataOutputStream out, Boolean o) throws IOException {
                out.writeBoolean(o);
            }

            @Override
            public Boolean read(DataInputStream in) throws IOException {
                return in.readBoolean();
            }
        }),
        STRING(String.class, new GenericHandler<String>() {
            @Override
            public void handling(DataOutputStream out, String o) throws IOException {
                out.writeUTF(o);
            }

            @Override
            public String read(DataInputStream in) throws IOException {
                return in.readUTF();
            }
        }),
        LINKED_MAP(LinkedHashMap.class, new MapHandler<>(LinkedHashMap::new)),
        MAP(Map.class, new MapHandler<>(HashMap::new)),
        Set(Set.class, new CollectionHandler<>(HashSet::new)),
        LIST(List.class, new CollectionHandler<>(ArrayList::new)),
        ;

        private final Class<?> generic;
        private final GenericHandler<?> handler;

        <G> Generic(Class<G> generic, GenericHandler<G> handler) {
            this.generic = generic;
            this.handler = handler;
        }

        private static Generic getBy(Object o) {
            for (Generic value : values()) {
                if (value.generic.isInstance(o)) return value;
            }

            throw new IllegalStateException("Object " + o + " is not a generic! Class: " + o.getClass().getName());
        }

        public static void write(DataOutputStream out, Object o) throws IOException {
            Generic g = getBy(o);
            out.writeByte(g.ordinal());
            g.handler.write(out, o);
        }

        public static Object read(DataInputStream in) throws IOException {
            int id = in.readUnsignedByte();
            return values()[id].handler.read(in);
        }

        private static class MapHandler<M extends Map> extends GenericHandler<M> {
            private final Supplier<M> mapInstance;

            public MapHandler(Supplier<M> mapInstance) {
                this.mapInstance = mapInstance;
            }

            @Override
            public void handling(DataOutputStream out, M o) throws IOException {
                Map<Object, Object> map = o;

                int size = map.size();

                //max unsigned short value
                if (size > 65535) throw new IllegalArgumentException("Cannot serialize maps with a size > 65.535!");

                out.writeShort(size);
                for (Map.Entry<Object, Object> e : map.entrySet()) {
                    SerializedGeneric key = new SerializedGeneric(e.getKey());
                    key.write(out);

                    SerializedGeneric value = new SerializedGeneric(e.getValue());
                    value.write(out);
                }
            }

            @Override
            public M read(DataInputStream in) throws IOException {
                Map<Object, Object> data = mapInstance.get();

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
            public C read(DataInputStream in) throws IOException {
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

            public abstract G read(DataInputStream in) throws IOException;
        }
    }
}

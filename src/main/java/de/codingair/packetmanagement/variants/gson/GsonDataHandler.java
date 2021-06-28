package de.codingair.packetmanagement.variants.gson;

import com.google.gson.*;
import de.codingair.packetmanagement.DataHandler;
import de.codingair.packetmanagement.exceptions.UnknownPacketException;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.FormedPacket;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.UUID;

public abstract class GsonDataHandler<C> extends DataHandler<C, String> {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .registerTypeAdapter(Packet.class, new PacketAdapter())
            .create();

    public GsonDataHandler(@NotNull String channelName, @NotNull Proxy proxy) {
        super(channelName, proxy);
        registering();
    }

    protected abstract void registering();

    @Override
    public @NotNull FormedPacket convertReceivedData(@NotNull String json, @Nullable C connection, @NotNull Direction direction) {
        return GSON.fromJson(json, FormedPacket.class);
    }

    @Override
    public String serializePacket(@NotNull Packet packet, boolean future, @Nullable UUID uuid) {
        return GSON.toJson(new FormedPacket(packet, future, uuid));
    }

    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.getDecoder().decode(json.getAsString());
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
        }
    }

    private static class PacketAdapter implements JsonSerializer<Packet>, JsonDeserializer<Packet> {
        @Override
        public Packet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject json = (JsonObject) jsonElement;

            String path = json.getAsJsonPrimitive("type").getAsString();
            try {
                @SuppressWarnings ("unchecked")
                Class<Packet> clazz = (Class<Packet>) Class.forName(path);

                String data = json.getAsJsonPrimitive("packet").getAsString();

                return GSON.fromJson(data, clazz);
            } catch (ClassNotFoundException e) {
                throw new UnknownPacketException("Cannot find packet: " + path);
            }
        }

        @Override
        public JsonElement serialize(Packet packet, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject json = new JsonObject();

            json.add("type", new JsonPrimitive(packet.getClass().getName()));
            json.add("packet", new JsonPrimitive(GSON.toJson(packet)));

            return json;
        }
    }
}

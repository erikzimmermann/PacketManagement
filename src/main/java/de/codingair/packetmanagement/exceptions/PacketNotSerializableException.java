package de.codingair.packetmanagement.exceptions;

public class PacketNotSerializableException extends PacketException {
    public PacketNotSerializableException(Class<?> c) {
        super("The packet " + c.getName() + " is not serializable.");
    }
}

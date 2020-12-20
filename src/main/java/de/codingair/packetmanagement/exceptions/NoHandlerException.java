package de.codingair.packetmanagement.exceptions;

public class NoHandlerException extends PacketException {
    public NoHandlerException(Class<?> clazz) {
        super("Cannot handle a packet without a handler: " + clazz);
    }
}

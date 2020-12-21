package de.codingair.packetmanagement.exceptions;

public class MalformedPacketException extends PacketException {
    public MalformedPacketException(String message, Throwable cause) {
        super(message, cause);
    }
}

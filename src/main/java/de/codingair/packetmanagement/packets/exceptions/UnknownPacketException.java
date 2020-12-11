package de.codingair.packetmanagement.packets.exceptions;

public class UnknownPacketException extends PacketException {
    public UnknownPacketException() {
    }

    public UnknownPacketException(String message) {
        super(message);
    }

    public UnknownPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPacketException(Throwable cause) {
        super(cause);
    }
}

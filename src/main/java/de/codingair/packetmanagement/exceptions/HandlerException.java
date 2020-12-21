package de.codingair.packetmanagement.exceptions;

public class HandlerException extends PacketException {
    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}

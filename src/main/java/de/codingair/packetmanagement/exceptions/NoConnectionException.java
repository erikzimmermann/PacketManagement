package de.codingair.packetmanagement.exceptions;

public class NoConnectionException extends PacketException {
    public NoConnectionException() {
    }

    public NoConnectionException(String message) {
        super(message);
    }
}

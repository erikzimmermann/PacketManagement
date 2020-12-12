package de.codingair.packetmanagement.packets.exceptions;

public class HandlerAlreadyPurgedException extends PacketException {
    public HandlerAlreadyPurgedException(String message) {
        super(message);
    }
}

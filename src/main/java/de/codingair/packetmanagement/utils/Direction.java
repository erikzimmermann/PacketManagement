package de.codingair.packetmanagement.utils;

public enum Direction {
    UP,
    DOWN,
    UNKNOWN
    ;

    public Direction inverse() {
        return this == UP ? DOWN : UP;
    }
}

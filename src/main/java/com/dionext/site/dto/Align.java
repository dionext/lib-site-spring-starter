package com.dionext.site.dto;

public enum Align {
    DEFAULT,
    LEFT,
    RIGHT,
    CENTER,
    JUST;

    public static final int SIZE = Integer.SIZE;

    public static Align forValue(int value) {
        return values()[value];
    }

    public int getValue() {
        return this.ordinal();
    }
}

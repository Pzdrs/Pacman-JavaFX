package me.petr.pacman;

import java.util.Random;

public enum GhostAIDirectionEnum {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static GhostAIDirectionEnum getRandom() {
        return values()[new Random().nextInt(values().length)];
    }
}


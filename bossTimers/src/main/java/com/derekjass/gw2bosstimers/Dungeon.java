package com.derekjass.gw2bosstimers;

import java.util.Arrays;

public class Dungeon {

    private final String name;
    private final String[] paths;

    public Dungeon(String[] data) {
        name = data[0];
        paths = new String[data.length - 1];
        System.arraycopy(data, 1, paths, 0, paths.length);
    }

    public String getName() {
        return name;
    }

    public String[] getPaths() {
        return Arrays.copyOf(paths, paths.length);
    }
}
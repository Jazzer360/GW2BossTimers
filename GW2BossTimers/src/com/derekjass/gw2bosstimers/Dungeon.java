package com.derekjass.gw2bosstimers;

import java.util.Arrays;

public class Dungeon {

	private final String name;
	private final String[] paths;

	public Dungeon(String[] data) {
		name = data[0];
		paths = new String[data.length - 1];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = data[i + 1];
		}
	}

	public String getName() {
		return name;
	}

	public String[] getPaths() {
		return Arrays.copyOf(paths, paths.length);
	}
}
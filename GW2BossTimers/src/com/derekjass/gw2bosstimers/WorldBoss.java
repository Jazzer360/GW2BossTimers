package com.derekjass.gw2bosstimers;

import java.util.Calendar;
import java.util.TimeZone;

public class WorldBoss {

	private Calendar cal;

	private final int level;
	private final String name;
	private final String region;
	private final String zone;
	private final String area;
	private final int[] spawnTimes;

	public WorldBoss(String[] bossInfo) {
		level = Integer.valueOf(bossInfo[0]);
		name = bossInfo[1];
		region = bossInfo[2];
		zone = bossInfo[3];
		area = bossInfo[4];
		spawnTimes = new int[bossInfo.length - 5];
		for (int i = 5; i < bossInfo.length; i++) {
			spawnTimes[i] = Integer.valueOf(bossInfo[i]);
		}
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public String getRegion() {
		return region;
	}

	public String getZone() {
		return zone;
	}

	public String getArea() {
		return area;
	}

	public long getPreviousSpawnTime(long time) {
		initCalendar();
		toMidnight(time);
		long midnight = cal.getTimeInMillis();

		for (int i = spawnTimes.length - 1; i >= 0; i--) {
			long spawn = midnight + spawnTimes[i] * 60 * 1000;
			if (spawn < time) {
				return spawn;
			}
		}

		long ms = (1440 - spawnTimes[spawnTimes.length - 1]) * 60 * 1000;

		return midnight - ms;
	}

	public long getNextSpawnTime(long time) {
		initCalendar();
		toMidnight(time);
		long midnight = cal.getTimeInMillis();

		for (int i = 0; i < spawnTimes.length; i++) {
			long spawn = midnight + spawnTimes[i] * 60 * 1000;
			if (spawn > time) {
				return spawn;
			}
		}

		long ms = (1440 + spawnTimes[0]) * 60 * 1000;

		return midnight + ms;
	}

	private void initCalendar() {
		if (cal == null) {
			cal = Calendar
					.getInstance(TimeZone.getTimeZone("America/New_York"));
		}
	}

	private void toMidnight(long date) {
		cal.setTimeInMillis(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
}

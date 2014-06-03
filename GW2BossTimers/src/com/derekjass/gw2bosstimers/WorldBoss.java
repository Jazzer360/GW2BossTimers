package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.BossTimerApplication.FIFTEEN_MINS;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;

public class WorldBoss {

	public static final Comparator<WorldBoss> COMPARATOR = new Comparator<WorldBoss>() {
		@Override
		public int compare(WorldBoss lhs, WorldBoss rhs) {
			long time = System.currentTimeMillis();
			if (time - lhs.getPreviousSpawnTime(time) < FIFTEEN_MINS) {
				return -1;
			}
			if (time - rhs.getPreviousSpawnTime(time) < FIFTEEN_MINS) {
				return 1;
			}
			long lhTime = lhs.getNextSpawnTime(time);
			long rhTime = rhs.getNextSpawnTime(time);
			return (int) (lhTime - rhTime);
		}
	};

	private Calendar mCalendar;

	private final String mLevel;
	private final String mName;
	private final String mRegion;
	private final String mZone;
	private final String mArea;
	private final int[] mSpawnTimes;

	public WorldBoss(String[] bossInfo) {
		mLevel = bossInfo[0];
		mName = bossInfo[1];
		mRegion = bossInfo[2];
		mZone = bossInfo[3];
		mArea = bossInfo[4];
		mSpawnTimes = new int[bossInfo.length - 5];
		for (int i = 5; i < bossInfo.length; i++) {
			mSpawnTimes[i - 5] = Integer.parseInt(bossInfo[i]);
		}
		Arrays.sort(mSpawnTimes);
	}

	public String getLevel() {
		return mLevel;
	}

	public String getName() {
		return mName;
	}

	public String getRegion() {
		return mRegion;
	}

	public String getZone() {
		return mZone;
	}

	public String getArea() {
		return mArea;
	}

	public long getPreviousSpawnTime(long time) {
		initCalendar();
		setCalendarToMidnight(time);
		long midnight = mCalendar.getTimeInMillis();

		for (int i = mSpawnTimes.length - 1; i >= 0; i--) {
			long spawn = midnight + mSpawnTimes[i] * 60 * 1000;
			if (spawn < time) {
				return spawn;
			}
		}

		long ms = (1440 - mSpawnTimes[mSpawnTimes.length - 1]) * 60 * 1000;

		return midnight - ms;
	}

	public long getNextSpawnTime(long time) {
		initCalendar();
		setCalendarToMidnight(time);
		long midnight = mCalendar.getTimeInMillis();

		for (int i = 0; i < mSpawnTimes.length; i++) {
			long spawn = midnight + mSpawnTimes[i] * 60 * 1000;
			if (spawn > time) {
				return spawn;
			}
		}

		long ms = (1440 + mSpawnTimes[0]) * 60 * 1000;

		return midnight + ms;
	}

	private void initCalendar() {
		if (mCalendar == null) {
			TimeZone gmt = TimeZone.getTimeZone("GMT");
			mCalendar = Calendar.getInstance(gmt);
		}
	}

	private void setCalendarToMidnight(long date) {
		mCalendar.setTimeInMillis(date);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
	}
}

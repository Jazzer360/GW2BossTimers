package com.derekjass.gw2bosstimers;

import java.util.Arrays;

import static com.derekjass.gw2bosstimers.ApplicationBossTimers.ONE_DAY;
import static com.derekjass.gw2bosstimers.ApplicationBossTimers.minutes;

public class Boss {

    private final String mLevel;
    private final String mName;
    private final String mRegion;
    private final String mZone;
    private final String mArea;
    private final int[] mSpawnTimes;

    public Boss(String[] bossInfo) {
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
        long midnight = time / ONE_DAY * ONE_DAY;

        for (int i = mSpawnTimes.length - 1; i >= 0; i--) {
            long spawn = midnight + minutes(mSpawnTimes[i]);
            if (spawn < time) {
                return spawn;
            }
        }

        long ms = ONE_DAY - minutes(mSpawnTimes[mSpawnTimes.length - 1]);

        return midnight - ms;
    }

    public long getNextSpawnTime(long time) {
        long midnight = time / ONE_DAY * ONE_DAY;

        for (int spawnTime : mSpawnTimes) {
            long spawn = midnight + minutes(spawnTime);
            if (spawn > time) {
                return spawn;
            }
        }

        long ms = ONE_DAY + minutes(mSpawnTimes[0]);

        return midnight + ms;
    }
}

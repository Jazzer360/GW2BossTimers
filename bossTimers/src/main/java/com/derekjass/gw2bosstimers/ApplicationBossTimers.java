package com.derekjass.gw2bosstimers;

import android.app.Application;

import java.util.Locale;

public class ApplicationBossTimers extends Application {

    public static final String PREF_LAST_KILL_PREFIX = "pref_last_kill_";
    public static final String PREF_LAST_PATH_PREFIX = "pref_last_path_";

    public static final long ONE_MINUTE = 60 * 1000;
    public static final long FIFTEEN_MINS = 15 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * 60 * ONE_MINUTE;

    public static long minutes(long mins) {
        return mins * ONE_MINUTE;
    }

    public static String getTimeString(long ms) {
        long hrs = ms / 1000 / 60 / 60;
        long mins = ms / 1000 / 60 % 60;
        long secs = ms / 1000 % 60;
        if (hrs > 0) {
            return String.format(Locale.US, "%d:%02d:%02d", hrs, mins, secs);
        } else {
            return String.format(Locale.US, "%d:%02d", mins, secs);
        }
    }
}

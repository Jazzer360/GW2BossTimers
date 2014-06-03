package com.derekjass.gw2bosstimers;

import android.app.Application;

public class BossTimerApplication extends Application {

	public static final String PREF_LAST_DISPLAY = "pref_last_display";
	public static final String PREF_LAST_KILL_PREFIX = "pref_last_kill_";

	public static final long FIFTEEN_MINS = 15 * 60 * 1000;
	public static final long ONE_DAY = 24 * 60 * 60 * 1000;
	public static final long THREE_DAYS = 3 * ONE_DAY;

}

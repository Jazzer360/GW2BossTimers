package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.BossTimerApplication.FIFTEEN_MINS;
import static com.derekjass.gw2bosstimers.BossTimerApplication.PREF_LAST_KILL_PREFIX;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BossListAdapter extends ArrayAdapter<WorldBoss> {

	private static class BossTimer extends CountDownTimer {

		private TextView mTime;

		public BossTimer(TextView timeView, WorldBoss boss) {
			super(getTimerDuration(boss), 1000);
			this.mTime = timeView;
			start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long timeToSpawn = millisUntilFinished - FIFTEEN_MINS;
			if (timeToSpawn > 0) {
				mTime.setText(getTimeString(timeToSpawn));
				mTime.setBackgroundColor(Color.TRANSPARENT);
			} else {
				mTime.setText(R.string.active);
				mTime.setBackgroundColor(Color.YELLOW);
			}
		}

		@Override
		public void onFinish() {
			mTime.setText("0:00");
			mTime.setBackgroundColor(Color.TRANSPARENT);
		}

		@SuppressLint("DefaultLocale")
		private static String getTimeString(long ms) {
			long hrs = ms / 1000 / 60 / 60;
			long mins = ms / 1000 / 60 % 60;
			long secs = ms / 1000 % 60;
			if (hrs > 0) {
				return String.format("%d:%02d:%02d", hrs, mins, secs);
			} else {
				return String.format("%d:%02d", mins, secs);
			}
		}

		private static long getTimerDuration(WorldBoss boss) {
			long time = System.currentTimeMillis();
			long prevSpawn = boss.getPreviousSpawnTime(time);
			if (time - prevSpawn < FIFTEEN_MINS) {
				return FIFTEEN_MINS - (time - prevSpawn);
			} else {
				return boss.getNextSpawnTime(time) - time + FIFTEEN_MINS;
			}
		}
	}

	private static class ViewHolder {
		private TextView bossName, level, region, zone, area, timeToSpawn,
				killedText;
		private BossTimer bossTimer;
		private PrefKeyListener listener;
	}

	private Context mContext;
	private Calendar mCalendar;
	private SharedPreferences mPrefs;

	public BossListAdapter(Context context, List<WorldBoss> objects) {
		super(context, R.layout.boss_list_item, objects);
		mContext = context;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder views;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.boss_list_item, null);
			views = new ViewHolder();
			views.bossName = (TextView) view.findViewById(R.id.bossName);
			views.level = (TextView) view.findViewById(R.id.level);
			views.region = (TextView) view.findViewById(R.id.region);
			views.zone = (TextView) view.findViewById(R.id.zone);
			views.area = (TextView) view.findViewById(R.id.area);
			views.timeToSpawn = (TextView) view.findViewById(R.id.timeToSpawn);
			views.killedText = (TextView) view.findViewById(R.id.killedToday);
			view.setTag(views);
		} else {
			views = (ViewHolder) view.getTag();
			views.bossTimer.cancel();
			views.bossTimer = null;
			mPrefs.unregisterOnSharedPreferenceChangeListener(views.listener);
			views.listener = null;
		}

		WorldBoss boss = getItem(position);

		views.bossName.setText(boss.getName());
		views.level.setText(boss.getLevel());
		views.region.setText(boss.getRegion());
		views.zone.setText(boss.getZone());
		views.area.setText(boss.getArea());
		views.killedText.setVisibility(isKilled(boss) ? View.VISIBLE
				: View.GONE);
		views.bossTimer = new BossTimer(views.timeToSpawn, boss);
		views.listener = new PrefKeyListener(getPrefKey(boss)) {
			@Override
			public void onPreferenceKeyChange(SharedPreferences prefs,
					String key) {
				long lastKilled = prefs.getLong(key, 0);
				boolean killed = lastKilled > getPreviousReset();
				views.killedText.setVisibility(killed ? View.VISIBLE
						: View.GONE);
			}
		};
		mPrefs.registerOnSharedPreferenceChangeListener(views.listener);

		return view;
	}

	public void setKilled(int position) {
		WorldBoss boss = getItem(position);
		if (isKilled(boss)) {
			mPrefs.edit().putLong(getPrefKey(boss), 0).commit();
		} else {
			mPrefs.edit().putLong(getPrefKey(boss), System.currentTimeMillis())
					.commit();
		}
	}

	private long getPreviousReset() {
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		return mCalendar.getTimeInMillis();
	}

	private boolean isKilled(WorldBoss boss) {
		long lastKill = mPrefs.getLong(getPrefKey(boss), 0);
		return lastKill > getPreviousReset() ? true : false;
	}

	private static String getPrefKey(WorldBoss boss) {
		return PREF_LAST_KILL_PREFIX + boss.getName();
	}
}
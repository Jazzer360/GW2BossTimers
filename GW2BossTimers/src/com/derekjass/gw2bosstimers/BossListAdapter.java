package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.BossTimerApplication.FIFTEEN_MINS;
import static com.derekjass.gw2bosstimers.BossTimerApplication.PREF_LAST_KILL_PREFIX;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BossListAdapter extends ArrayAdapter<WorldBoss> {

	private static class ViewHolder {
		private TextView bossName, level, region, zone, area, timeToSpawn,
				killedText;
		private OnSharedPreferenceChangeListener listener;
	}

	private Context mContext;
	private Calendar mCalendar;
	private SharedPreferences mPrefs;
	private CountDownTimer mTimer;

	public BossListAdapter(Context context, List<WorldBoss> objects) {
		super(context, R.layout.boss_list_item, objects);
		mContext = context;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	}

	public void startSorting() {
		final long time = System.currentTimeMillis();

		Comparator<WorldBoss> comparator = new Comparator<WorldBoss>() {
			@Override
			public int compare(WorldBoss lhs, WorldBoss rhs) {
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

		sort(comparator);
		mTimer = new CountDownTimer(getMsUntilSort(time), 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				notifyDataSetChanged();
			}

			@Override
			public void onFinish() {
				startSorting();
			}
		}.start();
	}

	public void stopSorting() {
		mTimer.cancel();
	}

	private long getMsUntilSort(long time) {
		WorldBoss boss = getItem(0);
		long lastSpawn = boss.getPreviousSpawnTime(time);

		if (time - lastSpawn < FIFTEEN_MINS) {
			return FIFTEEN_MINS - (time - lastSpawn);
		} else {
			return boss.getNextSpawnTime(time) - time + FIFTEEN_MINS;
		}
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
			mPrefs.unregisterOnSharedPreferenceChangeListener(views.listener);
			views.listener = null;
		}

		WorldBoss boss = getItem(position);
		long time = System.currentTimeMillis();
		long prevSpawn = boss.getPreviousSpawnTime(time);
		long nextSpawn = boss.getNextSpawnTime(time);
		boolean killed = isKilled(boss);

		views.bossName.setText(boss.getName());
		views.level.setText(boss.getLevel());
		views.region.setText(boss.getRegion());
		views.zone.setText(boss.getZone());
		views.area.setText(boss.getArea());
		if (time - prevSpawn < FIFTEEN_MINS) {
			views.timeToSpawn.setText(R.string.active);
			views.timeToSpawn.setBackgroundColor(Color.YELLOW);
		} else {
			views.timeToSpawn.setText(getTimeString(nextSpawn - time));
			views.timeToSpawn.setBackgroundColor(Color.TRANSPARENT);
		}
		views.killedText.setVisibility(killed ? View.VISIBLE : View.GONE);
		views.listener = new LongPreferenceChangeListener(getPrefKey(boss)) {
			@Override
			public void onSharedPreferenceChanged(long newValue) {
				boolean killed = isKilled(newValue);
				views.killedText.setVisibility(killed ? View.VISIBLE
						: View.GONE);
			}
		};

		mPrefs.registerOnSharedPreferenceChangeListener(views.listener);

		return view;
	}

	public void toggleKilled(int position) {
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

	private boolean isKilled(long lastKilled) {
		return lastKilled > getPreviousReset();
	}

	private boolean isKilled(WorldBoss boss) {
		long lastKill = mPrefs.getLong(getPrefKey(boss), 0);
		return isKilled(lastKill);
	}

	private static String getTimeString(long ms) {
		long hrs = ms / 1000 / 60 / 60;
		long mins = ms / 1000 / 60 % 60;
		long secs = ms / 1000 % 60;
		if (hrs > 0) {
			return String.format(Locale.US, "%d:%02d:%02d", hrs, mins, secs);
		} else {
			return String.format(Locale.US, "%d:%02d", mins, secs);
		}
	}

	private static String getPrefKey(WorldBoss boss) {
		return PREF_LAST_KILL_PREFIX + boss.getName();
	}
}
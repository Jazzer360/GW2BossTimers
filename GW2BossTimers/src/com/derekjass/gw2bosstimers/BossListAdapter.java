package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.ApplicationBossTimers.FIFTEEN_MINS;
import static com.derekjass.gw2bosstimers.ApplicationBossTimers.ONE_DAY;
import static com.derekjass.gw2bosstimers.ApplicationBossTimers.PREF_LAST_KILL_PREFIX;
import static com.derekjass.gw2bosstimers.ApplicationBossTimers.getTimeString;

import java.util.Comparator;
import java.util.List;

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

public class BossListAdapter extends ArrayAdapter<Boss> {

	private static class ViewHolder {
		private TextView bossName, level, region, zone, area, timeToSpawn,
				killedText;
		private OnSharedPreferenceChangeListener listener;
	}

	private SharedPreferences mPrefs;
	private CountDownTimer mTimer;

	public BossListAdapter(Context context, List<Boss> bosses) {
		super(context, R.layout.boss_list_item, bosses);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void startTimers() {
		final long time = System.currentTimeMillis();

		Comparator<Boss> comparator = new Comparator<Boss>() {
			@Override
			public int compare(Boss lhs, Boss rhs) {
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
				startTimers();
			}
		}.start();
	}

	public void stopTimers() {
		mTimer.cancel();
	}

	private long getMsUntilSort(long time) {
		Boss boss = getItem(0);
		long lastSpawn = boss.getPreviousSpawnTime(time);
		long timeSinceLastSpawn = time - lastSpawn;

		if (timeSinceLastSpawn < FIFTEEN_MINS) {
			return FIFTEEN_MINS - timeSinceLastSpawn;
		} else {
			return boss.getNextSpawnTime(time) - time + FIFTEEN_MINS;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder views;

		if (view == null) {
			view = getNewView(parent);
			views = setupHolder(view);
		} else {
			views = (ViewHolder) view.getTag();
			mPrefs.unregisterOnSharedPreferenceChangeListener(views.listener);
			views.listener = null;
		}

		Boss boss = getItem(position);
		long time = System.currentTimeMillis();
		long prevSpawn = boss.getPreviousSpawnTime(time);
		long nextSpawn = boss.getNextSpawnTime(time);
		boolean active = time - prevSpawn < FIFTEEN_MINS;
		boolean killed = isKilled(boss, active ? time : nextSpawn);

		views.bossName.setText(boss.getName());
		views.level.setText(boss.getLevel());
		views.region.setText(boss.getRegion());
		views.zone.setText(boss.getZone());
		views.area.setText(boss.getArea());
		if (active) {
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
				boolean killed = isKilled(newValue, System.currentTimeMillis());
				views.killedText.setVisibility(killed ? View.VISIBLE
						: View.GONE);
			}
		};

		mPrefs.registerOnSharedPreferenceChangeListener(views.listener);

		return view;
	}

	private View getNewView(ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.boss_list_item, parent, false);
	}

	private ViewHolder setupHolder(View view) {
		ViewHolder views = new ViewHolder();
		views.bossName = (TextView) view.findViewById(R.id.bossName);
		views.level = (TextView) view.findViewById(R.id.level);
		views.region = (TextView) view.findViewById(R.id.region);
		views.zone = (TextView) view.findViewById(R.id.zone);
		views.area = (TextView) view.findViewById(R.id.area);
		views.timeToSpawn = (TextView) view.findViewById(R.id.timeToSpawn);
		views.killedText = (TextView) view.findViewById(R.id.killedToday);
		view.setTag(views);
		return views;
	}

	public void toggleKilled(int position) {
		long time = System.currentTimeMillis();
		Boss boss = getItem(position);
		if (isKilled(boss, time)) {
			mPrefs.edit().putLong(getPrefKey(boss), 0).commit();
		} else {
			mPrefs.edit().putLong(getPrefKey(boss), time).commit();
		}
	}

	private long getPreviousReset(long time) {
		return time / ONE_DAY * ONE_DAY;
	}

	private boolean isKilled(long lastKilled, long time) {
		return lastKilled > getPreviousReset(time);
	}

	private boolean isKilled(Boss boss, long time) {
		long lastKill = mPrefs.getLong(getPrefKey(boss), 0);
		return isKilled(lastKill, time);
	}

	private static String getPrefKey(Boss boss) {
		return PREF_LAST_KILL_PREFIX + boss.getName();
	}
}
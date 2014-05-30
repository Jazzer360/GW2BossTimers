package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.BossTimerActivity.FIFTEEN_MINS;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BossListAdapter extends ArrayAdapter<WorldBoss> {

	private static class ViewHolder {
		private TextView bossName, level, region, zone, area, timeToSpawn;
	}

	private static class BossTimer extends CountDownTimer {

		private TextView time;

		public BossTimer(TextView timeView, WorldBoss boss) {
			super(getTimerDuration(boss), 1000);
			this.time = timeView;
			start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long timeToSpawn = millisUntilFinished - FIFTEEN_MINS;
			if (timeToSpawn > 0) {
				time.setText(getTimeString(timeToSpawn));
				time.setBackgroundColor(Color.TRANSPARENT);
			} else {
				time.setText(R.string.active);
				time.setBackgroundColor(Color.YELLOW);
			}
		}

		@Override
		public void onFinish() {
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

	private Context context;

	public BossListAdapter(Context context, List<WorldBoss> objects) {
		super(context, R.layout.boss_list_item, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.boss_list_item, null);
			BossListAdapter.ViewHolder views = new ViewHolder();
			views.bossName = (TextView) view.findViewById(R.id.bossName);
			views.level = (TextView) view.findViewById(R.id.level);
			views.region = (TextView) view.findViewById(R.id.region);
			views.zone = (TextView) view.findViewById(R.id.zone);
			views.area = (TextView) view.findViewById(R.id.area);
			views.timeToSpawn = (TextView) view.findViewById(R.id.timeToSpawn);
			view.setTag(views);
		} else {
			((CountDownTimer) view.getTag(R.id.boss_timer)).cancel();
			view.setTag(R.id.boss_timer, null);
		}

		WorldBoss boss = getItem(position);
		BossListAdapter.ViewHolder views = (BossListAdapter.ViewHolder) view
				.getTag();

		views.bossName.setText(boss.getName());
		views.level.setText(boss.getLevel());
		views.region.setText(boss.getRegion());
		views.zone.setText(boss.getZone());
		views.area.setText(boss.getArea());
		view.setTag(R.id.boss_timer, new BossTimer(views.timeToSpawn, boss));

		return view;
	}
}
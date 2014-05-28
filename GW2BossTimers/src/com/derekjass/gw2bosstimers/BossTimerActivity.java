package com.derekjass.gw2bosstimers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BossTimerActivity extends ActionBarActivity {

	private ListView listView;

	private List<WorldBoss> bosses = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boss_timer);
		setTitle(R.string.title_activity_boss_timer);
		listView = (ListView) findViewById(R.id.listView);

		String[] bossData = getResources().getStringArray(R.array.boss_data);

		for (String boss : bossData) {
			bosses.add(new WorldBoss(boss.split(",")));
		}

		BossListAdapter adapter = new BossListAdapter(this, bosses);
		adapter.sort();
		listView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		maybeShowDonateDialog();
	}

	private void maybeShowDonateDialog() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		long lastDisplay = prefs.getLong("pref_last_display", 0);
		long threeDays = 1000 * 60 * 60 * 24 * 3;
		if (System.currentTimeMillis() - threeDays > lastDisplay) {

			prefs.edit().putLong("pref_last_display",
					System.currentTimeMillis());
		}
	}

	private static class BossListAdapter extends ArrayAdapter<WorldBoss> {

		private static class ViewHolder {
			private TextView bossName, level, region, zone, area, timeToSpawn;
		}

		private static class BossTimer extends CountDownTimer {

			private TextView time;
			private BossListAdapter adapter;

			public BossTimer(TextView timeView, WorldBoss boss,
					BossListAdapter adapter) {
				super(getTimerDuration(boss), 1000);
				this.time = timeView;
				this.adapter = adapter;
				start();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				time.setText(getTimeString(millisUntilFinished));
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

			@Override
			public void onFinish() {
				adapter.sort();
			}

			private static long getTimerDuration(WorldBoss boss) {
				long time = System.currentTimeMillis();
				return boss.getNextSpawnTime(time) - time;
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
				ViewHolder views = new ViewHolder();
				views.bossName = (TextView) view.findViewById(R.id.bossName);
				views.level = (TextView) view.findViewById(R.id.level);
				views.region = (TextView) view.findViewById(R.id.region);
				views.zone = (TextView) view.findViewById(R.id.zone);
				views.area = (TextView) view.findViewById(R.id.area);
				views.timeToSpawn = (TextView) view
						.findViewById(R.id.timeToSpawn);
				view.setTag(views);
			} else {
				((CountDownTimer) view.getTag(R.id.boss_timer)).cancel();
				view.setTag(R.id.boss_timer, null);
			}

			WorldBoss boss = getItem(position);
			ViewHolder views = (ViewHolder) view.getTag();

			views.bossName.setText(boss.getName());
			views.level.setText(boss.getLevel());
			views.region.setText(boss.getRegion());
			views.zone.setText(boss.getZone());
			views.area.setText(boss.getArea());
			view.setTag(R.id.boss_timer, new BossTimer(views.timeToSpawn, boss,
					this));

			return view;
		}

		public void sort() {
			sort(new Comparator<WorldBoss>() {
				@Override
				public int compare(WorldBoss lhs, WorldBoss rhs) {
					long time = System.currentTimeMillis();
					long lhTime = lhs.getNextSpawnTime(time);
					long rhTime = rhs.getNextSpawnTime(time);
					return (int) (lhTime - rhTime);
				}
			});
		}
	}
}

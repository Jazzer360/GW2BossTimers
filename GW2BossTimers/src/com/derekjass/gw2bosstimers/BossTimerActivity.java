package com.derekjass.gw2bosstimers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

public class BossTimerActivity extends ActionBarActivity {

	static final long FIFTEEN_MINS = 15 * 60 * 1000;
	private static final Comparator<WorldBoss> COMPARATOR = new Comparator<WorldBoss>() {
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

	private Handler mHandler = new Handler();
	private ListView mListView;
	private BossListAdapter mAdapter;
	private List<WorldBoss> mBosses = new ArrayList<WorldBoss>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boss_timer);
		setTitle(R.string.title_activity_boss_timer);
		mListView = (ListView) findViewById(R.id.listView);

		String[] bossData = getResources().getStringArray(R.array.boss_data);

		for (String boss : bossData) {
			mBosses.add(new WorldBoss(boss.split(",")));
		}

		mAdapter = new BossListAdapter(this, mBosses);
		mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		maybeShowDonateDialog();
		mAdapter.sort(COMPARATOR);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mAdapter.sort(COMPARATOR);
				mHandler.postDelayed(this, getMsToNextRefresh());
			}
		}, getMsToNextRefresh());
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacksAndMessages(null);
	}

	private long getMsToNextRefresh() {
		WorldBoss boss = mBosses.get(0);
		long time = System.currentTimeMillis();
		long prevSpawn = boss.getPreviousSpawnTime(time);
		if (time - prevSpawn < FIFTEEN_MINS) {
			return FIFTEEN_MINS - (time - prevSpawn);
		}
		return boss.getNextSpawnTime(time) - time + FIFTEEN_MINS;
	}

	private void maybeShowDonateDialog() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		long lastDisplay = prefs.getLong("pref_last_display",
				System.currentTimeMillis());
		long threeDays = 1000 * 60 * 60 * 24 * 3;
		if (System.currentTimeMillis() - lastDisplay > threeDays) {

			prefs.edit().putLong("pref_last_display",
					System.currentTimeMillis());
		}
	}
}

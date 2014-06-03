package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.BossTimerApplication.FIFTEEN_MINS;
import static com.derekjass.gw2bosstimers.BossTimerApplication.PREF_LAST_DISPLAY;
import static com.derekjass.gw2bosstimers.BossTimerApplication.THREE_DAYS;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BossTimerActivity extends ActionBarActivity {

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

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.setKilled(position);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		maybeShowDonateDialog();
		mAdapter.sort(WorldBoss.COMPARATOR);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mAdapter.sort(WorldBoss.COMPARATOR);
				mHandler.postDelayed(this, getMsToNextSort());
			}
		}, getMsToNextSort());
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacksAndMessages(null);
	}

	private long getMsToNextSort() {
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
		long lastDisplay = prefs.getLong(PREF_LAST_DISPLAY,
				System.currentTimeMillis());

		if (System.currentTimeMillis() - lastDisplay > THREE_DAYS) {

			prefs.edit().putLong(PREF_LAST_DISPLAY, System.currentTimeMillis());
		}
	}
}

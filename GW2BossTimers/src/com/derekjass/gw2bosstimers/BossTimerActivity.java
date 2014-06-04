package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.BossTimerApplication.PREF_LAST_DISPLAY;
import static com.derekjass.gw2bosstimers.BossTimerApplication.THREE_DAYS;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BossTimerActivity extends ActionBarActivity {

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
				mAdapter.toggleKilled(position);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		maybeShowDonateDialog();
		mAdapter.startSorting();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.stopSorting();
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

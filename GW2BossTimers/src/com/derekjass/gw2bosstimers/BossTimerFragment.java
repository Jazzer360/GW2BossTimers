package com.derekjass.gw2bosstimers;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BossTimerFragment extends ListFragment {

	private BossListAdapter mAdapter;
	private List<WorldBoss> mBosses = new ArrayList<WorldBoss>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] bossData = getResources().getStringArray(R.array.boss_data);
		for (String boss : bossData) {
			mBosses.add(new WorldBoss(boss.split(",")));
		}

		mAdapter = new BossListAdapter(getActivity(), mBosses);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(mAdapter);

		ListView list = getListView();
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.toggleKilled(position);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.startSorting();
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.stopSorting();
	}
}

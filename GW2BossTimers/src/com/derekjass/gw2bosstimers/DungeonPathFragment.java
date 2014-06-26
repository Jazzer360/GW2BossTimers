package com.derekjass.gw2bosstimers;

import static com.derekjass.gw2bosstimers.ApplicationBossTimers.*;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class DungeonPathFragment extends Fragment {

	private List<Dungeon> mDungeons = new ArrayList<Dungeon>();
	private DungeonListAdapter mAdapter;
	private ListView mListView;
	private TextView mTimeToReset;
	private CountDownTimer mTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] dungeonData = getResources().getStringArray(
				R.array.dungeon_data);
		for (String data : dungeonData) {
			mDungeons.add(new Dungeon(data.split(",")));
		}

		mAdapter = new DungeonListAdapter(getActivity(), mDungeons);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dungeon, container,
				false);

		mListView = (ListView) view.findViewById(R.id.listView);
		mTimeToReset = (TextView) view.findViewById(R.id.timeToReset);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		startCountdown();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopCountdown();
	}

	private void startCountdown() {
		mAdapter.notifyDataSetChanged();
		mTimer = new CountDownTimer(getTimeToReset(), 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				mTimeToReset.setText(getString(R.string.time_to_reset)
						+ getTimeString(millisUntilFinished));
			}

			@Override
			public void onFinish() {
				mAdapter.notifyDataSetChanged();
				startCountdown();
			}
		}.start();
	}

	private void stopCountdown() {
		mTimer.cancel();
	}

	private static long getTimeToReset() {
		long time = System.currentTimeMillis();
		long prevReset = time / ONE_DAY * ONE_DAY;
		long nextReset = prevReset + ONE_DAY;
		return nextReset - time;
	}
}

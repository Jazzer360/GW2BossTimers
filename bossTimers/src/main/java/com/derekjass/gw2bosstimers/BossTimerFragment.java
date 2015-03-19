package com.derekjass.gw2bosstimers;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class BossTimerFragment extends ListFragment {

    private BossListAdapter mAdapter;
    private List<Boss> mBosses = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] bossData = getResources().getStringArray(R.array.boss_data);

        for (String boss : bossData) {
            mBosses.add(new Boss(boss.split(",")));
        }

        mAdapter = new BossListAdapter(getActivity(), mBosses);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {
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
        mAdapter.startTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.stopTimers();
    }
}

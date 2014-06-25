package com.derekjass.gw2bosstimers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;

public class BossTimerActivity extends ActionBarActivity implements TabListener {

	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boss_timer);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);

		mViewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int index) {
				switch (index) {
				case 0:
					return new BossTimerFragment();
				case 1:
					return new Fragment();
				default:
					return null;
				}
			}

			@Override
			public int getCount() {
				return 2;
			}
		});

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.boss_timers)).setTabListener(this));
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.dungeon_paths))
				.setTabListener(this));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}
}

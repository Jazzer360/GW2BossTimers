package com.derekjass.gw2bosstimers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupPager();

		setupActionBar();
	}

	private void setupPager() {
		mViewPager = (ViewPager) findViewById(R.id.viewPager);

		mViewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int index) {
				switch (index) {
				case 0:
					return new BossTimerFragment();
				case 1:
					return new DungeonPathFragment();
				default:
					return null;
				}
			}

			@Override
			public int getCount() {
				return 2;
			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int pos, float offset, int pixels) {
			}

			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
		});
	}

	private void setupActionBar() {
		TabListener tabListener = new TabListener() {
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}
		};

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.boss_timers))
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.dungeon_paths))
				.setTabListener(tabListener));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
	}
}

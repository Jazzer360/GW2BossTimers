package com.derekjass.gw2bosstimers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.derekjass.iabhelper.PurchaseStateFragment;
import com.derekjass.iabhelper.PurchaseStateFragment.PurchaseState;
import com.derekjass.iabhelper.SimplePurchaseStateFragment;
import com.derekjass.iabhelper.SimplePurchaseStateFragment.PurchaseStateListener;

public class MainActivity extends ActionBarActivity implements
		PurchaseStateListener {

	private static final String BANNER_PRODUCT_ID = "remove_banner";
	private static final String BANNER_FRAGMENT_TAG = "ad_fragment";
	private static final int BANNER_REQUEST_CODE = 0;

	private ViewPager mViewPager;
	private View mDonationBanner;
	private View mCloseButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDonationBanner = findViewById(R.id.donation_banner);
		mCloseButton = findViewById(R.id.close_button);
		setupCloseButton();
		addPurchaseStateFragment();

		setupPager();

		setupActionBar();
	}

	private void setupCloseButton() {
		mCloseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPurchaseStateFragment().purchaseProduct(BANNER_REQUEST_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case BANNER_REQUEST_CODE:
				getPurchaseStateFragment().handleActivityResult(data);
				break;
			}
		}
	}

	private void addPurchaseStateFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f = SimplePurchaseStateFragment.newInstance(BANNER_PRODUCT_ID,
				SimplePurchaseStateFragment.MANAGED_PRODUCT);
		ft.add(f, BANNER_FRAGMENT_TAG);
		ft.commit();
	}

	@Override
	public void onPurchaseStateChanged(String productId, PurchaseState state) {
		switch (state) {
		case UNKNOWN:
			setBannerAsVisible(true);
			mCloseButton.setVisibility(View.GONE);
			break;
		case NOT_PURCHASED:
			setBannerAsVisible(true);
			break;
		case PURCHASED:
			setBannerAsVisible(false);
			break;
		default:
		}
	}

	private void setBannerAsVisible(boolean visible) {
		int flag = visible ? View.VISIBLE : View.GONE;
		mDonationBanner.setVisibility(flag);
		mCloseButton.setVisibility(flag);
	}

	private PurchaseStateFragment getPurchaseStateFragment() {
		Fragment f = getSupportFragmentManager().findFragmentByTag(
				BANNER_FRAGMENT_TAG);
		return (PurchaseStateFragment) f;
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
			public void onPageScrollStateChanged(int state) {}

			@Override
			public void onPageScrolled(int pos, float offset, int pixels) {}

			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
		});
	}

	private void setupActionBar() {
		TabListener tabListener = new TabListener() {
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
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

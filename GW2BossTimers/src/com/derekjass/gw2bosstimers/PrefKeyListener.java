package com.derekjass.gw2bosstimers;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public abstract class PrefKeyListener implements
		OnSharedPreferenceChangeListener {

	private String mKey;

	public PrefKeyListener(String key) {
		mKey = key;
	}

	@Override
	public final void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key) {
		if (key.equals(mKey)) {
			onPreferenceKeyChange(sharedPreferences, key);
		}
	}

	public abstract void onPreferenceKeyChange(SharedPreferences prefs,
			String key);
}

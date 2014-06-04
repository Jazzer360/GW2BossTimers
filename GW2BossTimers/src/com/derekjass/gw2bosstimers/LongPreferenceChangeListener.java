package com.derekjass.gw2bosstimers;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public abstract class LongPreferenceChangeListener implements
		OnSharedPreferenceChangeListener {

	private String mKey;

	public LongPreferenceChangeListener(String key) {
		mKey = key;
	}

	@Override
	public final void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {
		if (mKey.equals(key)) {
			onSharedPreferenceChanged(prefs.getLong(key, 0));
		}
	}

	public abstract void onSharedPreferenceChanged(long newValue);
}

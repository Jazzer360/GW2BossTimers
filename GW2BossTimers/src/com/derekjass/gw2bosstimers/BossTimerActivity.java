package com.derekjass.gw2bosstimers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BossTimerActivity extends ActionBarActivity {

	private ListView listView;

	private List<WorldBoss> bosses = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boss_timer);
		listView = (ListView) findViewById(R.id.listView);

		String[] bossData = getResources().getStringArray(R.array.boss_data);

		for (String boss : bossData) {
			bosses.add(new WorldBoss(boss.split(",")));
		}
		
		listView.setAdapter(new BossListAdapter(this, bosses));
	}

	private static class BossListAdapter extends ArrayAdapter<WorldBoss> {

		private Context context;

		public BossListAdapter(Context context, List<WorldBoss> objects) {
			super(context, R.layout.boss_list_item, objects);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.boss_list_item, null);
			}

			WorldBoss boss = getItem(position);
			
			
			return view;
		}
	}
}

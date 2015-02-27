package com.derekjass.gw2bosstimers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.derekjass.gw2bosstimers.ApplicationBossTimers.ONE_DAY;
import static com.derekjass.gw2bosstimers.ApplicationBossTimers.PREF_LAST_PATH_PREFIX;

public class DungeonListAdapter extends ArrayAdapter<Dungeon> {

    private static class ViewHolder {
        private TextView dungeon;
        private LinearLayout paths;
    }

    private SharedPreferences mPrefs;

    public DungeonListAdapter(Context context, List<Dungeon> dungeons) {
        super(context, 0, dungeons);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder views;

        if (view == null) {
            view = getNewView(parent);
            views = setupHolder(view);
        } else {
            views = (ViewHolder) view.getTag();
        }

        Dungeon dungeon = getItem(position);

        views.dungeon.setText(dungeon.getName());
        setupCheckboxes(views.paths, dungeon);
        return view;
    }

    private void setupCheckboxes(LinearLayout layout, final Dungeon dungeon) {
        String[] paths = dungeon.getPaths();
        int numCheckboxes = layout.getChildCount() - 1;
        int pathCount = paths.length;

        while (numCheckboxes != pathCount) {
            if (numCheckboxes > pathCount) {
                layout.removeViewAt(numCheckboxes--);
            } else {
                layout.addView(new CheckBox(getContext()));
                numCheckboxes++;
            }
        }

        for (int i = 0; i < pathCount; i++) {
            final String path = paths[i];
            boolean complete = isPathComplete(dungeon, path);
            CheckBox box = (CheckBox) layout.getChildAt(i + 1);
            box.setOnCheckedChangeListener(null);
            box.setText(path);
            box.setChecked(complete);
            box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    long newValue = isChecked ? System.currentTimeMillis() : 0;
                    mPrefs.edit().putLong(getPrefKey(dungeon, path), newValue)
                            .apply();
                }
            });
        }
    }

    private boolean isPathComplete(Dungeon dungeon, String path) {
        long prevReset = System.currentTimeMillis() / ONE_DAY * ONE_DAY;
        long lastCompleted = mPrefs.getLong(getPrefKey(dungeon, path), 0);
        return lastCompleted > prevReset;
    }

    private View getNewView(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.dungeon_list_item, parent, false);
    }

    private ViewHolder setupHolder(View view) {
        ViewHolder views = new ViewHolder();
        views.dungeon = (TextView) view.findViewById(R.id.dungeonName);
        views.paths = (LinearLayout) view.findViewById(R.id.pathsLayout);
        view.setTag(views);
        return views;
    }

    private static String getPrefKey(Dungeon dungeon, String path) {
        return PREF_LAST_PATH_PREFIX + dungeon.getName() + path;
    }
}

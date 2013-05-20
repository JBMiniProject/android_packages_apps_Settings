/*
 * Copyright (C) 2012-2013 JBMini Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.jbminiproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.telephony.Phone;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.cyanogenmod.TouchInterceptor;

public class TileView extends SettingsPreferenceFragment {
    private static final String TAG = "Tiles";
    private static final String SEPARATOR = "OV=I=XseparatorX=I=VO";
//    private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";
//    private static final String UI_EXP_WIDGET_HAPTIC_FEEDBACK = "expanded_haptic_feedback";

//    private CheckBoxPreference mPowerWidgetHideOnChange;
//    private ListPreference mPowerWidgetHapticFeedback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {
            addPreferencesFromResource(R.xml.jbmini_tiles);
/*
            PreferenceScreen prefSet = getPreferenceScreen();

            mPowerWidgetHideOnChange = (CheckBoxPreference) prefSet
                    .findPreference(UI_EXP_WIDGET_HIDE_ONCHANGE);
            mPowerWidgetHapticFeedback = (ListPreference) prefSet
                    .findPreference(UI_EXP_WIDGET_HAPTIC_FEEDBACK);
            mPowerWidgetHapticFeedback.setOnPreferenceChangeListener(this);
            mPowerWidgetHapticFeedback.setSummary(mPowerWidgetHapticFeedback.getEntry());


            mPowerWidgetHideOnChange.setChecked((Settings.System.getInt(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_HIDE_ONCHANGE, 0) == 1));
            mPowerWidgetHapticFeedback.setValue(Integer.toString(Settings.System.getInt(
                    getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_HAPTIC_FEEDBACK, 2)));
*/
        }
    }
/*
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPowerWidgetHapticFeedback) {
            int intValue = Integer.parseInt((String) newValue);
            int index = mPowerWidgetHapticFeedback.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_HAPTIC_FEEDBACK, intValue);
            mPowerWidgetHapticFeedback.setSummary(mPowerWidgetHapticFeedback.getEntries()[index]);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mPowerWidgetHideOnChange) {
            value = mPowerWidgetHideOnChange.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_HIDE_ONCHANGE,
                    value ? 1 : 0);
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }
*/


    public static class TileViewChooser extends SettingsPreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        public TileViewChooser() {
        }

        private static final String TAG = "TileViewActivity";

        private static final String TILES_CATEGORY = "pref_tiles";
        private static final String SELECT_TILE_KEY_PREFIX = "pref_tile_";

        private static final String EXP_BRIGHTNESS_MODE = "pref_brightness_mode";
        private static final String EXP_NETWORK_MODE = "pref_network_mode";
        private static final String EXP_SCREENTIMEOUT_MODE = "pref_screentimeout_mode";
        private static final String EXP_RING_MODE = "pref_ring_mode";
        private static final String EXP_FLASH_MODE = "pref_flash_mode";
        private static final String PREF_USER_WIDGETS = "pref_user_widgets";

        private HashMap<CheckBoxPreference, String> mCheckBoxPrefs = new HashMap<CheckBoxPreference, String>();

        MultiSelectListPreference mBrightnessMode;
        ListPreference mNetworkMode;
        ListPreference mScreenTimeoutMode;
        MultiSelectListPreference mRingMode;
        ListPreference mFlashMode;
        EditTextPreference mUserNumbers;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            addPreferencesFromResource(R.xml.jbmini_tileview_widget);

            PreferenceScreen prefSet = getPreferenceScreen();
            PackageManager pm = getPackageManager();

            if (getActivity().getApplicationContext() == null) {
                return;
            }

            mUserNumbers = (EditTextPreference) prefSet.findPreference(PREF_USER_WIDGETS);
            if (mUserNumbers != null) {
                EditText numberEditText = mUserNumbers.getEditText();

                if (numberEditText != null) {
                    InputFilter lengthFilter = new InputFilter.LengthFilter(25);
                    numberEditText.setFilters(new InputFilter[]{lengthFilter});
                    numberEditText.setSingleLine(true);
                }
            }

            String userNumber = Settings.System.getString(getActivity().getApplicationContext().getContentResolver(), Settings.System.USER_MY_NUMBERS);

            if (userNumber == null || userNumber.equals("") || TextUtils.isEmpty(userNumber)) {
                userNumber = "000000000";
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(), Settings.System.USER_MY_NUMBERS, userNumber);
            }
            mUserNumbers.setText(userNumber);
            mUserNumbers.setOnPreferenceChangeListener(this);

            mBrightnessMode = (MultiSelectListPreference) prefSet.findPreference(EXP_BRIGHTNESS_MODE);
            String storedBrightnessMode = Settings.System.getString(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_BRIGHTNESS_MODE);
            if (storedBrightnessMode != null) {
                String[] brightnessModeArray = TextUtils.split(storedBrightnessMode, SEPARATOR);
                mBrightnessMode.setValues(new HashSet<String>(Arrays.asList(brightnessModeArray)));
                updateSummary(storedBrightnessMode, mBrightnessMode, R.string.pref_brightness_mode_summary);
            }
            mBrightnessMode.setOnPreferenceChangeListener(this);
            mNetworkMode = (ListPreference) prefSet.findPreference(EXP_NETWORK_MODE);
            mNetworkMode.setOnPreferenceChangeListener(this);
            mScreenTimeoutMode = (ListPreference) prefSet.findPreference(EXP_SCREENTIMEOUT_MODE);
            mScreenTimeoutMode.setOnPreferenceChangeListener(this);
            mRingMode = (MultiSelectListPreference) prefSet.findPreference(EXP_RING_MODE);
            String storedRingMode = Settings.System.getString(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_RING_MODE);
            if (storedRingMode != null) {
                String[] ringModeArray = TextUtils.split(storedRingMode, SEPARATOR);
                mRingMode.setValues(new HashSet<String>(Arrays.asList(ringModeArray)));
                updateSummary(storedRingMode, mRingMode, R.string.pref_ring_mode_summary);
            }
            mRingMode.setOnPreferenceChangeListener(this);
            mFlashMode = (ListPreference) prefSet.findPreference(EXP_FLASH_MODE);
            mFlashMode.setOnPreferenceChangeListener(this);

            // Update the summary text
            mNetworkMode.setSummary(mNetworkMode.getEntry());
            mScreenTimeoutMode.setSummary(mScreenTimeoutMode.getEntry());
            mFlashMode.setSummary(mFlashMode.getEntry());

            // Add the available tiles to the list
            PreferenceCategory prefTiles = (PreferenceCategory) prefSet.findPreference(TILES_CATEGORY);

            // empty our preference category and set it to order as added
            prefTiles.removeAll();
            prefTiles.setOrderingAsAdded(false);

            // emtpy our checkbox map
            mCheckBoxPrefs.clear();

            // get our list of tiles
            ArrayList<String> tileList = TileViewUtil.getTileListFromString(TileViewUtil.getCurrentTiles(getActivity().getApplicationContext()));

            // Don't show mobile data options if not supported
            boolean isMobileData = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
            if (!isMobileData) {
                TileViewUtil.TILES.remove(TileViewUtil.TILE_MOBILEDATA);
                TileViewUtil.TILES.remove(TileViewUtil.TILE_NETWORKMODE);
                TileViewUtil.TILES.remove(TileViewUtil.TILE_WIFIAP);
                prefTiles.removePreference(mNetworkMode);
            }

            // fill that checkbox map!
            for (TileViewUtil.TileInfo tile : TileViewUtil.TILES.values()) {
                // create a checkbox
                CheckBoxPreference cb = new CheckBoxPreference(getActivity().getApplicationContext());

                // set a dynamic key based on tile id
                cb.setKey(SELECT_TILE_KEY_PREFIX + tile.getId());

                // set vanity info
                cb.setTitle(tile.getTitleResId());

                // set our checked state
                if (tileList.contains(tile.getId())) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }

                // add to our prefs set
                mCheckBoxPrefs.put(cb, tile.getId());

                // specific checks for availability on some platforms
                if (TileViewUtil.TILE_TORCH.equals(tile.getId()) &&
                        !getResources().getBoolean(R.bool.has_led_flash)) { // disable flashlight if it's not supported
                    cb.setEnabled(false);
                    mFlashMode.setEnabled(false);
                } else if (TileViewUtil.TILE_NETWORKMODE.equals(tile.getId())) {
                    // some phones run on networks not supported by this button, so disable it
                    int network_state = -99;

                    try {
                        network_state = Settings.Secure.getInt(getActivity()
                                .getApplicationContext().getContentResolver(),
                                Settings.Secure.PREFERRED_NETWORK_MODE);
                    } catch (Settings.SettingNotFoundException e) {
                        Log.e(TAG, "Unable to retrieve PREFERRED_NETWORK_MODE", e);
                    }

                    switch (network_state) {
                    // list of supported network modes
                        case Phone.NT_MODE_WCDMA_PREF:
                        case Phone.NT_MODE_WCDMA_ONLY:
                        case Phone.NT_MODE_GSM_UMTS:
                        case Phone.NT_MODE_GSM_ONLY:
                            break;
                        default:
                            cb.setEnabled(false);
                            break;
                    }
                }

                // add to the category
                prefTiles.addPreference(cb);
            }
        }

        private void usersWidgets() {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("User Tile Notice");
            alert.setMessage(getResources().getString(R.string.userwidgets_message));
            alert.setPositiveButton(com.android.internal.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            alert.show();
        }

        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                Preference preference) {
            // we only modify the tile list if it was one of our checks that was clicked
            boolean tileWasModified = false;
            ArrayList<String> tileList = new ArrayList<String>();
            for (Map.Entry<CheckBoxPreference, String> entry : mCheckBoxPrefs.entrySet()) {
                if (entry.getKey().isChecked()) {
                    tileList.add(entry.getValue());
                }

                if (preference == entry.getKey()) {
                    tileWasModified = true;
                }
            }

            if (tileWasModified) {
                // now we do some wizardry and reset the tile list
                TileViewUtil.saveCurrentTiles(getActivity().getApplicationContext(),
                        TileViewUtil.mergeInNewTileString(
                                TileViewUtil.getCurrentTiles(getActivity()
                                        .getApplicationContext()), TileViewUtil
                                        .getTileStringFromList(tileList)));
                return true;
            }

            return false;
        }

        private class MultiSelectListPreferenceComparator implements Comparator<String> {
            private MultiSelectListPreference pref;

            MultiSelectListPreferenceComparator(MultiSelectListPreference p) {
                pref = p;
            }

            @Override
            public int compare(String lhs, String rhs) {
                return Integer.compare(pref.findIndexOfValue(lhs),
                        pref.findIndexOfValue(rhs));
            }
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mBrightnessMode) {
                ArrayList<String> arrValue = new ArrayList<String>((Set<String>) newValue);
                Collections.sort(arrValue, new MultiSelectListPreferenceComparator(mBrightnessMode));
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_BRIGHTNESS_MODE, TextUtils.join(SEPARATOR, arrValue));
                updateSummary(TextUtils.join(SEPARATOR, arrValue),
                        mBrightnessMode, R.string.pref_brightness_mode_summary);
            } else if (preference == mNetworkMode) {
                int value = Integer.valueOf((String) newValue);
                int index = mNetworkMode.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_NETWORK_MODE, value);
                mNetworkMode.setSummary(mNetworkMode.getEntries()[index]);
            } else if (preference == mScreenTimeoutMode) {
                int value = Integer.valueOf((String) newValue);
                int index = mScreenTimeoutMode.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_SCREENTIMEOUT_MODE, value);
                mScreenTimeoutMode.setSummary(mScreenTimeoutMode.getEntries()[index]);
            } else if (preference == mRingMode) {
                ArrayList<String> arrValue = new ArrayList<String>((Set<String>) newValue);
                Collections.sort(arrValue, new MultiSelectListPreferenceComparator(mRingMode));
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_RING_MODE, TextUtils.join(SEPARATOR, arrValue));
                updateSummary(TextUtils.join(SEPARATOR, arrValue), mRingMode, R.string.pref_ring_mode_summary);
            } else if (preference == mFlashMode) {
                int value = Integer.valueOf((String) newValue);
                int index = mFlashMode.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_FLASH_MODE, value);
                mFlashMode.setSummary(mFlashMode.getEntries()[index]);
            } else if (preference == mUserNumbers) {
                String userNumbers = String.valueOf(newValue);
                if (userNumbers == null || userNumbers.equals("")|| TextUtils.isEmpty(userNumbers)) {
                    userNumbers = "000000000";
                }
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(),
                            Settings.System.USER_MY_NUMBERS, userNumbers);
                mUserNumbers.setSummary(userNumbers);
                usersWidgets();
            }
            return true;
        }

        private void updateSummary(String val, MultiSelectListPreference pref, int defSummary) {
            // Update summary message with current values
            final String[] values = parseStoredValue(val);
            if (values != null) {
                final int length = values.length;
                final CharSequence[] entries = pref.getEntries();
                StringBuilder summary = new StringBuilder();
                for (int i = 0; i < (length); i++) {
                    CharSequence entry = entries[Integer.parseInt(values[i])];
                    if ((length - i) > 2) {
                        summary.append(entry).append(", ");
                    } else if ((length - i) == 2) {
                        summary.append(entry).append(" & ");
                    } else if ((length - i) == 1) {
                        summary.append(entry);
                    }
                }
                pref.setSummary(summary);
            } else {
                pref.setSummary(defSummary);
            }
        }

        public static String[] parseStoredValue(CharSequence val) {
            if (TextUtils.isEmpty(val)) {
                return null;
            } else {
                return val.toString().split(SEPARATOR);
            }
        }

    }



    public static class TileViewOrder extends ListFragment
    {
        private static final String TAG = "TileViewOrderActivity";

        private ListView mTileList;
        private TileAdapter mTileAdapter;
        View mContentView = null;
        Context mContext;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.order_tileview_activity, null);
            return mContentView;
        }

        // Called when the activity is first created.
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mContext = getActivity().getApplicationContext();

            mTileList = getListView();
            ((TouchInterceptor) mTileList).setDropListener(mDropListener);
            mTileAdapter = new TileAdapter(mContext);
            setListAdapter(mTileAdapter);
        }

        @Override
        public void onDestroy() {
            ((TouchInterceptor) mTileList).setDropListener(null);
            setListAdapter(null);
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
            // reload our tiles and invalidate the views for redraw
            mTileAdapter.reloadTiles();
            mTileList.invalidateViews();
        }

        private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
            public void drop(int from, int to) {
                // get the current tile list
                ArrayList<String> tiles = TileViewUtil.getTileListFromString(
                        TileViewUtil.getCurrentTiles(mContext));

                // move the tile
                if (from < tiles.size()) {
                    String tile = tiles.remove(from);

                    if (to <= tiles.size()) {
                        tiles.add(to, tile);

                        // save our tiles
                        TileViewUtil.saveCurrentTiles(mContext,
                                TileViewUtil.getTileStringFromList(tiles));

                        // tell our adapter/listview to reload
                        mTileAdapter.reloadTiles();
                        mTileList.invalidateViews();
                    }
                }
            }
        };

        private class TileAdapter extends BaseAdapter {
            private Context mContext;
            private Resources mSystemUIResources = null;
            private LayoutInflater mInflater;
            private ArrayList<TileViewUtil.TileInfo> mTiles;

            public TileAdapter(Context c) {
                mContext = c;
                mInflater = LayoutInflater.from(mContext);

                PackageManager pm = mContext.getPackageManager();
                if (pm != null) {
                    try {
                        mSystemUIResources = pm.getResourcesForApplication("com.android.systemui");
                    } catch (Exception e) {
                        mSystemUIResources = null;
                        Log.e(TAG, "Could not load SystemUI resources", e);
                    }
                }

                reloadTiles();
            }

            public void reloadTiles() {
                ArrayList<String> tiles = TileViewUtil.getTileListFromString(
                        TileViewUtil.getCurrentTiles(mContext));

                mTiles = new ArrayList<TileViewUtil.TileInfo>();
                for (String tile : tiles) {
                    if (TileViewUtil.TILES.containsKey(tile)) {
                        mTiles.add(TileViewUtil.TILES.get(tile));
                    }
                }
            }

            public int getCount() {
                return mTiles.size();
            }

            public Object getItem(int position) {
                return mTiles.get(position);
            }

            public long getItemId(int position) {
                return position;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                final View v;
                if (convertView == null) {
                    v = mInflater.inflate(R.layout.order_tileview_list_item, null);
                } else {
                    v = convertView;
                }

                TileViewUtil.TileInfo tile = mTiles.get(position);

                final TextView name = (TextView) v.findViewById(R.id.name);
                final ImageView icon = (ImageView) v.findViewById(R.id.icon);

                name.setText(tile.getTitleResId());

                // assume no icon first
                icon.setVisibility(View.GONE);

                // attempt to load the icon for this button
                if (mSystemUIResources != null) {
                    int resId = mSystemUIResources.getIdentifier(tile.getIcon(), null, null);
                    if (resId > 0) {
                        try {
                            Drawable d = mSystemUIResources.getDrawable(resId);
                            icon.setVisibility(View.VISIBLE);
                            icon.setImageDrawable(d);
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving icon drawable", e);
                        }
                    }
                }

                return v;
            }
        }
    }
}

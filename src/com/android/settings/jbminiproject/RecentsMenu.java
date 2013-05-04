/*
 * Copyright (C) 2012-2013 JBMiniProject
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.jbminiproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.os.Handler;
import android.util.Log;
import android.net.Uri;

import android.view.IWindowManager;
import android.os.IBinder;
import android.os.IPowerManager;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;
import net.margaritov.preference.colorpicker.ColorPickerPreference; 

import java.util.Date;

public class RecentsMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String SENSE4_STYLE_RECENTS_PROP = "pref_sense4_style_recents";
    private static final String RAM_BAR_MODE = "ram_bar_mode";
    private static final String RAM_BAR_COLOR_APP_MEM = "ram_bar_color_app_mem";
    private static final String RAM_BAR_COLOR_CACHE_MEM = "ram_bar_color_cache_mem";
    private static final String RAM_BAR_COLOR_TOTAL_MEM = "ram_bar_color_total_mem";

    static final int DEFAULT_MEM_COLOR = 0xff8d8d8d;
    static final int DEFAULT_CACHE_COLOR = 0xff00aa00;
    static final int DEFAULT_ACTIVE_APPS_COLOR = 0xff33b5e5;

    private SwitchPreference mSense4RecentsPref;
    private ListPreference mRamBarModePref;
    private ColorPickerPreference mRamBarAppMemColorPref;
    private ColorPickerPreference mRamBarCacheMemColorPref;
    private ColorPickerPreference mRamBarTotalMemColorPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_recents);

        PreferenceScreen prefSet = getPreferenceScreen();

        mSense4RecentsPref = (SwitchPreference) prefSet.findPreference(SENSE4_STYLE_RECENTS_PROP);
        mRamBarModePref = (ListPreference) prefSet.findPreference(RAM_BAR_MODE);
        mRamBarModePref.setOnPreferenceChangeListener(this);
        mRamBarAppMemColorPref = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_APP_MEM);
        mRamBarAppMemColorPref.setOnPreferenceChangeListener(this);
        mRamBarCacheMemColorPref = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_CACHE_MEM);
        mRamBarCacheMemColorPref.setOnPreferenceChangeListener(this);
        mRamBarTotalMemColorPref = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_TOTAL_MEM);
        mRamBarTotalMemColorPref.setOnPreferenceChangeListener(this);


        updateSense4Recents();
        updateRamBarMode();
        updateRamBarAppMemColor();
        updateRamBarCacheMemColor();
        updateRamBarTotalMemColor();
        updateRamBarOptions();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    /* Update functions */
    private void updateSense4Recents() {
        mSense4RecentsPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SENSE4_RECENT_APPS, 0) == 1);
        mSense4RecentsPref.setOnPreferenceChangeListener(this);
    }

    private void updateRamBarMode() {
        int ramBarModePref = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.RECENTS_RAM_BAR_MODE, 0);
        mRamBarModePref.setValue(String.valueOf(ramBarModePref));
        mRamBarModePref.setSummary(mRamBarModePref.getEntry());
        mRamBarModePref.setOnPreferenceChangeListener(this);
    }
    private void updateRamBarAppMemColor() {
        int intColor;
        String hexColor;
        intColor = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, DEFAULT_ACTIVE_APPS_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRamBarAppMemColorPref.setSummary(hexColor);
    }
    private void updateRamBarCacheMemColor() {
        int intColor;
        String hexColor;
        intColor = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, DEFAULT_CACHE_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRamBarCacheMemColorPref.setSummary(hexColor);
    }
    private void updateRamBarTotalMemColor() {
        int intColor;
        String hexColor;
        intColor = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR_MEM_COLOR, DEFAULT_MEM_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRamBarTotalMemColorPref.setSummary(hexColor);
    }


    /* Write functions */
    private void writeSense4Recents(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SENSE4_RECENT_APPS, (Boolean) NewVal ? 1 : 0);
        Helpers.restartSystemUI();
    }

    private void writeRamBarMode(Object NewVal) {
        int ramBarMode = Integer.valueOf((String) NewVal);
        int index = mRamBarModePref.findIndexOfValue((String) NewVal);
        Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.RECENTS_RAM_BAR_MODE, ramBarMode);
        mRamBarModePref.setSummary(mRamBarModePref.getEntries()[index]);
        updateRamBarOptions();
    }

    private void writeRamBarAppMemColor(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        mRamBarAppMemColorPref.setSummary(hex);

        int intHex = ColorPickerPreference.convertToColorInt(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, intHex);
    }

    private void writeRamBarCacheMemColor(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        mRamBarCacheMemColorPref.setSummary(hex);

        int intHex = ColorPickerPreference.convertToColorInt(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, intHex);
    }

    private void writeRamBarTotalMemColor(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        mRamBarTotalMemColorPref.setSummary(hex);

        int intHex = ColorPickerPreference.convertToColorInt(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR_MEM_COLOR, intHex);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSense4RecentsPref) {
            writeSense4Recents(newValue);
            return true;
        } else if (preference == mRamBarModePref) {
            writeRamBarMode(newValue);
            return true;
        } else if (preference == mRamBarAppMemColorPref) {
            writeRamBarAppMemColor(newValue);
            return true;
        } else if (preference == mRamBarCacheMemColorPref) {
            writeRamBarCacheMemColor(newValue);
            return true;
        } else if (preference == mRamBarTotalMemColorPref) {
            writeRamBarTotalMemColor(newValue);
            return true;
        }
        return false;
    }


    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    private void updateRamBarOptions() {
        int ramBarMode = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR_MODE, 0);
        if (ramBarMode == 0) {
            mRamBarAppMemColorPref.setEnabled(false);
            mRamBarCacheMemColorPref.setEnabled(false);
            mRamBarTotalMemColorPref.setEnabled(false);
        } else if (ramBarMode == 1) {
            mRamBarAppMemColorPref.setEnabled(true);
            mRamBarCacheMemColorPref.setEnabled(false);
            mRamBarTotalMemColorPref.setEnabled(false);
        } else if (ramBarMode == 2) {
            mRamBarAppMemColorPref.setEnabled(true);
            mRamBarCacheMemColorPref.setEnabled(true);
            mRamBarTotalMemColorPref.setEnabled(false);
        } else {
            mRamBarAppMemColorPref.setEnabled(true);
            mRamBarCacheMemColorPref.setEnabled(true);
            mRamBarTotalMemColorPref.setEnabled(true);
        }
    }
}

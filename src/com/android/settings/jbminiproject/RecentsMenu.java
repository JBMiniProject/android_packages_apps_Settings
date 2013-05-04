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

public class RecentsMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String SENSE4_STYLE_RECENTS_PROP = "pref_sense4_style_recents";
    private static final String RECENTS_RAM_BAR_PROP = "pref_recents_ram_bar";

    private SwitchPreference mSense4RecentsPref;
    private SwitchPreference mRamBarPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_recents);

        PreferenceScreen prefSet = getPreferenceScreen();

        mSense4RecentsPref = (SwitchPreference) prefSet.findPreference(SENSE4_STYLE_RECENTS_PROP);
        mRamBarPref = (SwitchPreference) prefSet.findPreference(RECENTS_RAM_BAR_PROP);


        updateSense4Recents();
        updateRamBar();
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

    private void updateRamBar() {
        mRamBarPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR, 0) == 1);
        mRamBarPref.setOnPreferenceChangeListener(this);
    }


    /* Write functions */
    private void writeSense4Recents(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SENSE4_RECENT_APPS, (Boolean) NewVal ? 1 : 0);
        Helpers.restartSystemUI();
    }

    private void writeRamBar(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.RECENTS_RAM_BAR, (Boolean) NewVal ? 1 : 0);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSense4RecentsPref) {
            writeSense4Recents(newValue);
            return true;
        } else if (preference == mRamBarPref) {
            writeRamBar(newValue);
            return true;
        }
        return false;
    }
}

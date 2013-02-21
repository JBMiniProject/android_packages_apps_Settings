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
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.os.Handler;
import android.util.Log;
import android.net.Uri;

import android.text.Spannable;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.IPowerManager;
import android.widget.EditText;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.SystemProperties;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ScreenshotMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String SCREENSHOT_SOUND_PREF = "screenshot_sound_pref";
    private static final String SCREENSHOT_DELAY_PREF = "screenshot_delay_pref";

    private CheckBoxPreference mScreenshotSoundPref;
    private ListPreference mScreenshotDelayPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_screenshot);

        PreferenceScreen prefSet = getPreferenceScreen();

        mScreenshotSoundPref = (CheckBoxPreference) prefSet.findPreference(SCREENSHOT_SOUND_PREF);
        mScreenshotDelayPref = (ListPreference) prefSet.findPreference(SCREENSHOT_DELAY_PREF);

        updateScreenshotSound();
        updateScreenshotDelay();
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
    private void updateScreenshotSound() {
        mScreenshotSoundPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREENSHOT_SOUND, 1) == 1);
    }

    private void updateScreenshotDelay() {
        mScreenshotDelayPref.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREENSHOT_DELAY, 1000) + "");
        mScreenshotDelayPref.setOnPreferenceChangeListener(this);
    }


    /* Write functions */
    private void writeScreenshotSound() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREENSHOT_SOUND, mScreenshotSoundPref.isChecked() ? 1 : 0);
    }

    private void writeScreenshotDelay(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREENSHOT_DELAY, Integer.parseInt((String) NewVal));
        updateScreenshotDelay();
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mScreenshotSoundPref) {
            writeScreenshotSound();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mScreenshotDelayPref) {
            writeScreenshotDelay(newValue);
        }
        return false;
    }
}

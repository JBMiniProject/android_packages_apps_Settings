/*
* Copyright (C) 2008 The Android Open Source Project
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

package com.android.settings;

import java.io.IOException;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.preference.PreferenceScreen;
import android.os.Handler;
import android.util.Log;
import android.net.Uri;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.IPowerManager;

import android.provider.Settings;
import android.os.SystemProperties;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JBMiniSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "JBMP_Settings";
    private static final boolean DEBUG = true;

    private static final String DISABLE_BOOTANIMATION_PREF = "pref_disable_boot_animation";
    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";

    private static final String RAISED_BRIGHTNESS = "pref_raisedbrightness";
    private static final String RAISED_BRIGHTNESS_PROP = "sys.raisedbrightness";
    private static final String RAISED_BRIGHTNESS_PERSIST_PROP = "persist.sys.raisedbrightness";
    private static final int RAISED_BRIGHTNESS_DEFAULT = 0;

    private final Configuration mCurrentConfig = new Configuration();

    private CheckBoxPreference mDisableBootanimPref;

    private CheckBoxPreference mRaisedBrightnessPref;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.jbmini_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mDisableBootanimPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTANIMATION_PREF);
        mDisableBootanimPref.setChecked("1".equals(SystemProperties.get(DISABLE_BOOTANIMATION_PERSIST_PROP, "0")));

        mRaisedBrightnessPref = (CheckBoxPreference) prefSet.findPreference(RAISED_BRIGHTNESS);
        mRaisedBrightnessPref.setChecked("1".equals(SystemProperties.get(RAISED_BRIGHTNESS_PERSIST_PROP, "0")));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mDisableBootanimPref) {
            SystemProperties.set(DISABLE_BOOTANIMATION_PERSIST_PROP, mDisableBootanimPref.isChecked() ? "1" : "0");
        }
        else if (preference == mRaisedBrightnessPref) {
            SystemProperties.set(RAISED_BRIGHTNESS_PERSIST_PROP, mRaisedBrightnessPref.isChecked() ? "1" : "0");
            Utils.fileWriteOneLine("/sys/devices/platform/i2c-adapter/i2c-0/0-0036/mode", mRaisedBrightnessPref.isChecked() ? "i2c_pwm" : "i2c_pwm_als");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
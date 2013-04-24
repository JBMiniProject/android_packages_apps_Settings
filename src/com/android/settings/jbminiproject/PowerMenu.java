/*
* Copyright (C) 2012-2013 JBMiniProject
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

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
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

public class PowerMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String DISABLE_REBOOT_PROP = "pref_disable_reboot";
    private static final String DISABLE_EXPANDED_DESKTOP_PROP = "pref_disable_expanded_desktop";
    private static final String DISABLE_SCREENSHOT_PROP = "pref_disable_screenshot";
    private static final String DISABLE_AIRPLANE_PROP = "pref_disable_airplane";
    private static final String DISABLE_RINGER_PROP = "pref_disable_ringer";
    private static final String DISABLE_TITLE_PROP = "pref_disable_title";

    private SwitchPreference mDisableRebootPref;
    private SwitchPreference mDisableScreenshotPref;
    private SwitchPreference mExpandedDesktopPref;
    private SwitchPreference mDisableAirplanePref;
    private SwitchPreference mDisableRingerPref;
    private SwitchPreference mDisableTitlePref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_powermenu);

        PreferenceScreen prefSet = getPreferenceScreen();

        mDisableRebootPref = (SwitchPreference) prefSet.findPreference(DISABLE_REBOOT_PROP);
        mDisableScreenshotPref = (SwitchPreference) prefSet.findPreference(DISABLE_SCREENSHOT_PROP);
        mExpandedDesktopPref = (SwitchPreference) prefSet.findPreference(DISABLE_EXPANDED_DESKTOP_PROP);
        mDisableAirplanePref = (SwitchPreference) prefSet.findPreference(DISABLE_AIRPLANE_PROP);
        mDisableRingerPref = (SwitchPreference) prefSet.findPreference(DISABLE_RINGER_PROP);
        mDisableTitlePref = (SwitchPreference) prefSet.findPreference(DISABLE_TITLE_PROP);


        updateDisableReboot();
        updateDisableScreenshot();
        updateDisableExpandedDesktop();
        updateDisableAirplane();
        updateDisableRinger();
        updateDisableTitle();
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
    private void updateDisableReboot() {
        mDisableRebootPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_REBOOT, 1) == 1);
        mDisableRebootPref.setOnPreferenceChangeListener(this);
    }

    private void updateDisableScreenshot() {
        mDisableScreenshotPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, 1) == 1);
        mDisableScreenshotPref.setOnPreferenceChangeListener(this);
    }

    private void updateDisableExpandedDesktop() {
        mExpandedDesktopPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0) == 1);
        mExpandedDesktopPref.setOnPreferenceChangeListener(this);
    }

    private void updateDisableAirplane() {
        mDisableAirplanePref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_AIRPLANE, 1) == 1);
        mDisableAirplanePref.setOnPreferenceChangeListener(this);
    }

    private void updateDisableRinger() {
        mDisableRingerPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_RINGER, 1) == 1);
        mDisableRingerPref.setOnPreferenceChangeListener(this);
    }

    private void updateDisableTitle() {
        mDisableTitlePref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_TITLE, 1) == 1);
        mDisableTitlePref.setOnPreferenceChangeListener(this);
    }


    /* Write functions */
    private void writeDisableReboot(Object value) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_REBOOT, (Boolean) value ? 1 : 0);
    }

    private void writeDisableScreenshot(Object value) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, (Boolean) value ? 1 : 0);
    }

    private void writeDisableExpandedDesktop(Object value) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, (Boolean) value ? 1 : 0);
    }

    private void writeDisableAirplane(Object value) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_AIRPLANE, (Boolean) value ? 1 : 0);
    }

    private void writeDisableRinger(Object value) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_RINGER, (Boolean) value ? 1 : 0);
    }

    private void writeDisableTitle(Object value) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_TITLE, (Boolean) value ? 1 : 0);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mDisableRebootPref) {
            writeDisableReboot(value);
            return true;
        } else if (preference == mDisableScreenshotPref) {
            writeDisableScreenshot(value);
            return true;
        } else if (preference == mExpandedDesktopPref) {
            writeDisableExpandedDesktop(value);
            return true;
        } else if (preference == mDisableAirplanePref) {
            writeDisableAirplane(value);
            return true;
        } else if (preference == mDisableRingerPref) {
            writeDisableRinger(value);
            return true;
        } else if (preference == mDisableTitlePref) {
            writeDisableTitle(value);
            return true;
        }
        return false;
    }
}

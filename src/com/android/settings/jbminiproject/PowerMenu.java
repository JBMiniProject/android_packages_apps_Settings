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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
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

public class PowerMenu extends SettingsPreferenceFragment {

    private static final String DISABLE_REBOOT_PROP = "pref_disable_reboot";
    private static final String DISABLE_EXPANDED_DESKTOP_PROP = "pref_disable_expanded_desktop";
    private static final String DISABLE_SCREENSHOT_PROP = "pref_disable_screenshot";
    private static final String DISABLE_AIRPLANE_PROP = "pref_disable_airplane";
    private static final String DISABLE_RINGER_PROP = "pref_disable_ringer";
    private static final String DISABLE_TITLE_PROP = "pref_disable_title";

    private CheckBoxPreference mDisableRebootPref;
    private CheckBoxPreference mDisableScreenshotPref;
    private CheckBoxPreference mExpandedDesktopPref;
    private CheckBoxPreference mDisableAirplanePref;
    private CheckBoxPreference mDisableRingerPref;
    private CheckBoxPreference mDisableTitlePref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_powermenu);

        PreferenceScreen prefSet = getPreferenceScreen();

        mDisableRebootPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_REBOOT_PROP);
        mDisableScreenshotPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_SCREENSHOT_PROP);
        mExpandedDesktopPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_EXPANDED_DESKTOP_PROP);
        mDisableAirplanePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_AIRPLANE_PROP);
        mDisableRingerPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_RINGER_PROP);
        mDisableTitlePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_TITLE_PROP);


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
    }

    private void updateDisableScreenshot() {
        mDisableScreenshotPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, 1) == 1);
    }

    private void updateDisableExpandedDesktop() {
        mExpandedDesktopPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0) == 1);
    }

    private void updateDisableAirplane() {
        mDisableAirplanePref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_AIRPLANE, 1) == 1);
    }

    private void updateDisableRinger() {
        mDisableRingerPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_RINGER, 1) == 1);
    }

    private void updateDisableTitle() {
        mDisableTitlePref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_TITLE, 1) == 1);
    }


    /* Write functions */
    private void writeDisableReboot() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_REBOOT, mDisableRebootPref.isChecked() ? 1 : 0);
    }

    private void writeDisableScreenshot() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, mDisableScreenshotPref.isChecked() ? 1 : 0);
    }

    private void writeDisableExpandedDesktop() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, mExpandedDesktopPref.isChecked() ? 1 : 0);
    }

    private void writeDisableAirplane() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_AIRPLANE, mDisableAirplanePref.isChecked() ? 1 : 0);
    }

    private void writeDisableRinger() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_RINGER, mDisableRingerPref.isChecked() ? 1 : 0);
    }

    private void writeDisableTitle() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_TITLE, mDisableTitlePref.isChecked() ? 1 : 0);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mDisableRebootPref) {
            writeDisableReboot();
        } else if (preference == mDisableScreenshotPref) {
            writeDisableScreenshot();
        } else if (preference == mExpandedDesktopPref) {
            writeDisableExpandedDesktop();
        } else if (preference == mDisableAirplanePref) {
            writeDisableAirplane();
        } else if (preference == mDisableRingerPref) {
            writeDisableRinger();
        } else if (preference == mDisableTitlePref) {
            writeDisableTitle();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}

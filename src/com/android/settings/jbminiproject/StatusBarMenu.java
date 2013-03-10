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
import android.app.AlertDialog;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.os.Handler;
import android.util.Log;
import android.net.Uri;
import android.preference.Preference.OnPreferenceClickListener;
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

public class StatusBarMenu extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String CENTER_CLOCK_STATUS_BAR_PROP = "pref_center_clock_status_bar";
    private static final String CLOCK_WEEKDAY_PROP = "pref_clock_weekday";
    private static final String CLOCK_COLOR_PICKER_PROP = "pref_clock_color";
    private static final String STATUSBAR_TRANSPARENCY_PROP = "pref_statusbar_transparency";
    private static final String NOTIFICATION_SHOW_WIFI_SSID_PROP = "pref_notification_show_wifi_ssid";

    private CheckBoxPreference mCenterClockStatusBarPref;
    private ListPreference mClockWeekdayPref;
    private ColorPickerPreference mClockColorPicker;
    private ListPreference mStatusbarTransparencyPref;
    private CheckBoxPreference mShowWifiNamePref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCenterClockStatusBarPref = (CheckBoxPreference) prefSet.findPreference(CENTER_CLOCK_STATUS_BAR_PROP);
        mClockWeekdayPref = (ListPreference) prefSet.findPreference(CLOCK_WEEKDAY_PROP);
        mClockWeekdayPref.setOnPreferenceChangeListener(this);
        mClockColorPicker = (ColorPickerPreference) prefSet.findPreference(CLOCK_COLOR_PICKER_PROP);
        mClockColorPicker.setOnPreferenceChangeListener(this);
        mStatusbarTransparencyPref = (ListPreference) prefSet.findPreference(STATUSBAR_TRANSPARENCY_PROP);
        mStatusbarTransparencyPref.setOnPreferenceChangeListener(this);
        mShowWifiNamePref = (CheckBoxPreference) prefSet.findPreference(NOTIFICATION_SHOW_WIFI_SSID_PROP);

        updateCenterClockStatusBar();
        updateClockWeekday();
        updateStatusbarTransparency();
        updateWifiName();
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
    private void updateCenterClockStatusBar() {
        mCenterClockStatusBarPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, 0) == 1);
    }

    private void updateClockWeekday() {
        mClockWeekdayPref.setValue(Integer.toString(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_WEEKDAY, 0)));
        mClockWeekdayPref.setOnPreferenceChangeListener(this);
    }

    private void updateStatusbarTransparency() {
        mStatusbarTransparencyPref.setValue((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_TRANSPARENCY, 100)) + "");
        mStatusbarTransparencyPref.setOnPreferenceChangeListener(this);
    }

    private void updateWifiName() {
        mShowWifiNamePref.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(), Settings.System.NOTIFICATION_SHOW_WIFI_SSID, false));
    }


    /* Write functions */
    private void writeCenterClockStatusBar() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, mCenterClockStatusBarPref.isChecked() ? 1 : 0);
        Helpers.restartSystemUI();
    }

    private void writeClockWeekday(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_WEEKDAY, Integer.parseInt((String) NewVal));
        updateClockWeekday();
    }

    private void writeClockColorPicker(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        mClockColorPicker.setSummary(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_COLOR, ColorPickerPreference.convertToColorInt(hex));
        mClockColorPicker.setOnPreferenceChangeListener(this);
    }

    private void writeStatusbarTransparency(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_TRANSPARENCY, Integer.valueOf((String) NewVal));
        updateStatusbarTransparency();
    }

    private void writeWifiName() {
        Settings.System.putBoolean(getActivity().getContentResolver(), Settings.System.NOTIFICATION_SHOW_WIFI_SSID, mShowWifiNamePref.isChecked());
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCenterClockStatusBarPref) {
            writeCenterClockStatusBar();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mClockColorPicker) {
            writeClockColorPicker(newValue);
        } else if (preference == mClockWeekdayPref) {
            writeClockWeekday(newValue);
        } else if (preference == mStatusbarTransparencyPref) {
            writeStatusbarTransparency(newValue);
        } else if (preference == mShowWifiNamePref) {
            writeWifiName();
            return true;
        }
        return false;
    }
}

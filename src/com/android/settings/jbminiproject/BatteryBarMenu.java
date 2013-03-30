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
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.os.Handler;
import android.util.Log;
import android.net.Uri;
import android.preference.Preference.OnPreferenceClickListener;
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

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class BatteryBarMenu extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String BATT_BAR_PROP = "pref_battery_bar";
    private static final String BATT_BAR_STYLE_PROP = "pref_battery_bar_style";
    private static final String BATT_BAR_COLOR_PROP = "pref_battery_bar_color";
    private static final String BATT_BAR_WIDTH_PROP = "pref_battery_bar_thickness";
    private static final String BATT_ANIMATE_PROP = "pref_battery_bar_animate";

    private CheckBoxPreference mBatteryBarPref;
    private ListPreference mBatteryBarStylePref;
    private ListPreference mBatteryBarThicknessPref;
    private CheckBoxPreference mBatteryBarChargingAnimationPref;
    private ColorPickerPreference mBatteryBarColor;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_batterybar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mBatteryBarPref = (CheckBoxPreference) prefSet.findPreference(BATT_BAR_PROP);
        mBatteryBarStylePref = (ListPreference) prefSet.findPreference(BATT_BAR_STYLE_PROP);
        mBatteryBarStylePref.setOnPreferenceChangeListener(this);
        mBatteryBarColor = (ColorPickerPreference) prefSet.findPreference(BATT_BAR_COLOR_PROP);
        mBatteryBarColor.setOnPreferenceChangeListener(this);
        mBatteryBarChargingAnimationPref = (CheckBoxPreference) findPreference(BATT_ANIMATE_PROP);
        mBatteryBarThicknessPref = (ListPreference) prefSet.findPreference(BATT_BAR_WIDTH_PROP);
        mBatteryBarThicknessPref.setOnPreferenceChangeListener(this);

        updateBatteryBar();
        updateBatteryBarStyle();
        updateBatteryBarChargAnim();
        updateBatteryBarThickness();
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
    private void updateBatteryBar() {
        mBatteryBarPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1);
        boolean status = mBatteryBarPref.isChecked();
        if (status) {
            mBatteryBarStylePref.setEnabled(true);
            mBatteryBarChargingAnimationPref.setEnabled(true);
            mBatteryBarThicknessPref.setEnabled(true);
            mBatteryBarColor.setEnabled(true);
        } else {
            mBatteryBarStylePref.setEnabled(false);
            mBatteryBarChargingAnimationPref.setEnabled(false);
            mBatteryBarThicknessPref.setEnabled(false);
            mBatteryBarColor.setEnabled(false);
        }
    }

    private void updateBatteryBarStyle() {
        mBatteryBarStylePref.setValue((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0)) + "");
        mBatteryBarStylePref.setOnPreferenceChangeListener(this);
    }

    private void updateBatteryBarChargAnim() {
        mBatteryBarChargingAnimationPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);
    }

    private void updateBatteryBarThickness() {
        mBatteryBarThicknessPref.setValue((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1)) + "");
        mBatteryBarThicknessPref.setOnPreferenceChangeListener(this);
    }


    /* Write functions */
    private void writeBatteryBar() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR, mBatteryBarPref.isChecked() ? 1 : 0);
        updateBatteryBar();
    }

    private void writeBatteryBarStyle(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_STYLE, Integer.parseInt((String) NewVal));
        updateBatteryBarStyle();
    }

    private void writeBatteryBarColor(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        mBatteryBarColor.setSummary(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_COLOR, ColorPickerPreference.convertToColorInt(hex));
        mBatteryBarColor.setOnPreferenceChangeListener(this);
    }

    private void writeBatteryBarChargAnim() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, mBatteryBarChargingAnimationPref.isChecked() ? 1 : 0);
    }

    private void writeBatteryBarThickness(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, Integer.parseInt((String) NewVal));
        updateBatteryBarThickness();
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mBatteryBarPref) {
            writeBatteryBar();
        } else if (preference == mBatteryBarChargingAnimationPref) {
            writeBatteryBarChargAnim();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBatteryBarColor) {
            writeBatteryBarColor(newValue);
        } else if (preference == mBatteryBarStylePref) {
            writeBatteryBarStyle(newValue);
        } else if (preference == mBatteryBarThicknessPref) {
            writeBatteryBarThickness(newValue);
        }

        return false;
    }
}
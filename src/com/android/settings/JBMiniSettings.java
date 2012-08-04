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
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;

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

    private static final String BACK_BUTTON_ENDS_CALL_PROP = "pref_back_button_ends_call";
    private static final String HOME_BUTTON_ANSWERS_CALL_PROP = "pref_home_button_answers_call";
    private static final String CENTER_CLOCK_STATUS_BAR_PROP = "pref_center_clock_status_bar";
    private static final String KEY_VOLUME_ADJUST_SOUNDS_PROP = "pref_volume_adjust_sounds";

    private static final String DISABLE_REBOOT_PROP = "pref_disable_reboot";
    private static final String DISABLE_SCREENSHOT_PROP = "pref_disable_screenshot";
    private static final String DISABLE_AIRPLANE_PROP = "pref_disable_airplane";
    private static final String DISABLE_RINGER_PROP = "pref_disable_ringer";
    private static final String DISABLE_TITLE_PROP = "pref_disable_title";

    private final Configuration mCurrentConfig = new Configuration();

    private CheckBoxPreference mDisableBootanimPref;
    private CheckBoxPreference mRaisedBrightnessPref;

    private CheckBoxPreference mBackButtonEndsCallPref;
    private CheckBoxPreference mHomeButtonAnswersCall;
    private CheckBoxPreference mCenterClockStatusBar;
    private CheckBoxPreference mVolumeAdjustSounds;

    private CheckBoxPreference mDisableRebootPref;
    private CheckBoxPreference mDisableScreenshotPref;
    private CheckBoxPreference mDisableAirplanePref;
    private CheckBoxPreference mDisableRingerPref;
    private CheckBoxPreference mDisableTitlePref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "\n\nWelcome in JB Mini Project's custom settings!!!\n\n");

        addPreferencesFromResource(R.xml.jbmini_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mDisableBootanimPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTANIMATION_PREF);
        mRaisedBrightnessPref = (CheckBoxPreference) prefSet.findPreference(RAISED_BRIGHTNESS);

        mBackButtonEndsCallPref = (CheckBoxPreference) prefSet.findPreference(BACK_BUTTON_ENDS_CALL_PROP);
        mHomeButtonAnswersCall = (CheckBoxPreference) prefSet.findPreference(HOME_BUTTON_ANSWERS_CALL_PROP);
        mCenterClockStatusBar = (CheckBoxPreference) prefSet.findPreference(CENTER_CLOCK_STATUS_BAR_PROP);
        mVolumeAdjustSounds = (CheckBoxPreference) prefSet.findPreference(KEY_VOLUME_ADJUST_SOUNDS_PROP);
        mVolumeAdjustSounds.setPersistent(false);

        mDisableRebootPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_REBOOT_PROP);
        mDisableScreenshotPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_SCREENSHOT_PROP);
        mDisableAirplanePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_AIRPLANE_PROP);
        mDisableRingerPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_RINGER_PROP);
        mDisableTitlePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_TITLE_PROP);

        if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ANSWERS_CALL_TRICK, 0) < 5) {
            prefSet..removePreference(findPreference(HOME_BUTTON_ANSWERS_CALL_PROP));
        }

        updateDisableBootAnimation();
        updateRaisedBrightness();
        updateBackButtonEndsCall();
        updateHomeButtonAnswersCall();
        updateCenterClockStatusBar();
        updateVolumeAdjustSound();
        updateDisableReboot();
        updateDisableScreenshot();
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
    private void updateDisableBootAnimation() {
        mDisableBootanimPref.setChecked("1".equals(SystemProperties.get(DISABLE_BOOTANIMATION_PERSIST_PROP, "0")));
    }

    private void updateRaisedBrightness() {
        mRaisedBrightnessPref.setChecked("1".equals(SystemProperties.get(RAISED_BRIGHTNESS_PERSIST_PROP, "0")));
    }

    private void updateBackButtonEndsCall() {
        mBackButtonEndsCallPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.BACK_BUTTON_ENDS_CALL, 0) == 1);
    }

    private void updateHomeButtonAnswersCall() {
        mHomeButtonAnswersCall.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.HOME_BUTTON_ANSWERS_CALL, 0) == 1);
    }

    private void updateCenterClockStatusBar() {
        mCenterClockStatusBar.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, 0) == 1);
    }

    private void updateVolumeAdjustSound() {
        mVolumeAdjustSounds.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, 1) != 0);
    }

    private void updateDisableReboot() {
        mDisableRebootPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_REBOOT, 1) == 1);
    }

    private void updateDisableScreenshot() {
        mDisableScreenshotPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, 1) == 1);
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
    private void writeDisableBootAnimation() {
        SystemProperties.set(DISABLE_BOOTANIMATION_PERSIST_PROP, mDisableBootanimPref.isChecked() ? "1" : "0");
    }

    private void writeRaisedBrightness() {
        SystemProperties.set(RAISED_BRIGHTNESS_PERSIST_PROP, mRaisedBrightnessPref.isChecked() ? "1" : "0");
        Utils.fileWriteOneLine("/sys/devices/platform/i2c-adapter/i2c-0/0-0036/mode", mRaisedBrightnessPref.isChecked() ? "i2c_pwm" : "i2c_pwm_als");
    }

    private void writeBackButtonEndsCall() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.BACK_BUTTON_ENDS_CALL, mBackButtonEndsCallPref.isChecked() ? 1 : 0);
    }

    private void writeHomeButtonAnswersCall() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.HOME_BUTTON_ANSWERS_CALL, mHomeButtonAnswersCall.isChecked() ? 1 : 0);
    }

    private void writeCenterClockStatusBar() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, mCenterClockStatusBar.isChecked() ? 1 : 0);
        Helpers.restartSystemUI();
    }

    private void writeVolumeAdjustSound() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, mVolumeAdjustSounds.isChecked() ? 1 : 0);
    }

    private void writeDisableReboot() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_REBOOT, mDisableRebootPref.isChecked() ? 1 : 0);
    }

    private void writeDisableScreenshot() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, mDisableScreenshotPref.isChecked() ? 1 : 0);
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
        if (preference == mDisableBootanimPref) {
            writeDisableBootAnimation();
        } else if (preference == mRaisedBrightnessPref) {
            writeRaisedBrightness();
        } else if (preference == mBackButtonEndsCallPref) {
            writeBackButtonEndsCall();
        } else if (preference == mHomeButtonAnswersCall) {
            writeHomeButtonAnswersCall();
        } else if (preference == mCenterClockStatusBar) {
            writeCenterClockStatusBar();
        } else if (preference == mVolumeAdjustSounds) {
            writeVolumeAdjustSound();
        } else if (preference == mDisableRebootPref) {
            writeDisableReboot();
        } else if (preference == mDisableScreenshotPref) {
            writeDisableScreenshot();
        } else if (preference == mDisableAirplanePref) {
            writeDisableAirplane();
        } else if (preference == mDisableRingerPref) {
            writeDisableRinger();
        } else if (preference == mDisableTitlePref) {
            writeDisableTitle();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
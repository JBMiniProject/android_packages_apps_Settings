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
import android.text.Spannable;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.IPowerManager;
import android.widget.EditText;

import android.provider.Settings;
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

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class JBMiniSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "JBMP_Settings";
    private static final boolean DEBUG = true;

    private static final String CENTER_CLOCK_STATUS_BAR_PROP = "pref_center_clock_status_bar";
    private static final String CLOCK_WEEKDAY_PROP = "pref_clock_weekday";
    private static final String DISABLE_ALARM_PROP = "pref_disable_alarm";
    private static final String CLOCK_COLOR_PICKER_PROP = "pref_clock_color";

    private static final String BATT_BAR_PROP = "pref_battery_bar_list";
    private static final String BATT_BAR_STYLE_PROP = "pref_battery_bar_style";
    private static final String BATT_BAR_COLOR_PROP = "pref_battery_bar_color";
    private static final String BATT_BAR_WIDTH_PROP = "pref_battery_bar_thickness";
    private static final String BATT_ANIMATE_PROP = "pref_battery_bar_animate";

    private static final String DISABLE_BOOTANIMATION_PREF = "pref_disable_boot_animation";
    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";
    private static final String DISABLE_BOOTAUDIO_PROP = "pref_disable_bootaudio";
    private static final String DISABLE_BUGMAILER_PROP = "pref_disable_bugmailer";
    private static final String RAISED_BRIGHTNESS = "pref_raisedbrightness";
    private static final String RAISED_BRIGHTNESS_PROP = "sys.raisedbrightness";
    private static final String RAISED_BRIGHTNESS_PERSIST_PROP = "persist.sys.raisedbrightness";

    private static final String BACK_BUTTON_ENDS_CALL_PROP = "pref_back_button_ends_call";
    private static final String HOME_BUTTON_ANSWERS_CALL_PROP = "pref_home_button_answers_call";
    private static final String KEY_VOLUME_ADJUST_SOUNDS_PROP = "pref_volume_adjust_sounds";

    private static final String DISABLE_REBOOT_PROP = "pref_disable_reboot";
    private static final String DISABLE_SCREENSHOT_PROP = "pref_disable_screenshot";
    private static final String DISABLE_AIRPLANE_PROP = "pref_disable_airplane";
    private static final String DISABLE_RINGER_PROP = "pref_disable_ringer";
    private static final String DISABLE_TITLE_PROP = "pref_disable_title";

    private static final String CUSTOM_CARRIER_LABEL_PROP = "pref_carrier_label";
    private static final String LOCKSCREEN_TEXT_COLOR_PROP = "pref_lockscreen_text_color";

    private final Configuration mCurrentConfig = new Configuration();

    private CheckBoxPreference mCenterClockStatusBar;
    private ListPreference mClockWeekday;
    private CheckBoxPreference mDisableAlarmPref;
    private ColorPickerPreference mClockColorPicker;

    private ListPreference mBatteryBar;
    private ListPreference mBatteryBarStyle;
    private ListPreference mBatteryBarThickness;
    private CheckBoxPreference mBatteryBarChargingAnimation;
    private ColorPickerPreference mBatteryBarColor;

    private CheckBoxPreference mDisableBootanimPref;
    private CheckBoxPreference mDisableBootAudioPref;
    private CheckBoxPreference mDisableBugmailerPref;
    private CheckBoxPreference mRaisedBrightnessPref;

    private CheckBoxPreference mBackButtonEndsCallPref;
    private CheckBoxPreference mHomeButtonAnswersCall;
    private CheckBoxPreference mVolumeAdjustSounds;

    private CheckBoxPreference mDisableRebootPref;
    private CheckBoxPreference mDisableScreenshotPref;
    private CheckBoxPreference mDisableAirplanePref;
    private CheckBoxPreference mDisableRingerPref;
    private CheckBoxPreference mDisableTitlePref;

    private Preference mCustomCarrierLabel;
    private String mCustomCarrierLabelSummary = null;
    private ColorPickerPreference mLockscreenTextColor;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "\n\nWelcome in JB Mini Project's custom settings!!!\n\n");

        addPreferencesFromResource(R.xml.jbmini_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCenterClockStatusBar = (CheckBoxPreference) prefSet.findPreference(CENTER_CLOCK_STATUS_BAR_PROP);
        mClockWeekday = (ListPreference) prefSet.findPreference(CLOCK_WEEKDAY_PROP);
        mClockWeekday.setOnPreferenceChangeListener(this);
        mDisableAlarmPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_ALARM_PROP);
        mClockColorPicker = (ColorPickerPreference) prefSet.findPreference(CLOCK_COLOR_PICKER_PROP);
        mClockColorPicker.setOnPreferenceChangeListener(this);

        mBatteryBar = (ListPreference) prefSet.findPreference(BATT_BAR_PROP);
        mBatteryBar.setOnPreferenceChangeListener(this);
        mBatteryBarStyle = (ListPreference) prefSet.findPreference(BATT_BAR_STYLE_PROP);
        mBatteryBarStyle.setOnPreferenceChangeListener(this);
        mBatteryBarColor = (ColorPickerPreference) prefSet.findPreference(BATT_BAR_COLOR_PROP);
        mBatteryBarColor.setOnPreferenceChangeListener(this);
        mBatteryBarChargingAnimation = (CheckBoxPreference) findPreference(BATT_ANIMATE_PROP);
        mBatteryBarThickness = (ListPreference) prefSet.findPreference(BATT_BAR_WIDTH_PROP);
        mBatteryBarThickness.setOnPreferenceChangeListener(this);

        mDisableBootanimPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTANIMATION_PREF);

        mDisableBootAudioPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTAUDIO_PROP);
        mDisableBugmailerPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BUGMAILER_PROP);
        mRaisedBrightnessPref = (CheckBoxPreference) prefSet.findPreference(RAISED_BRIGHTNESS);

        mBackButtonEndsCallPref = (CheckBoxPreference) prefSet.findPreference(BACK_BUTTON_ENDS_CALL_PROP);
        mHomeButtonAnswersCall = (CheckBoxPreference) prefSet.findPreference(HOME_BUTTON_ANSWERS_CALL_PROP);
        mVolumeAdjustSounds = (CheckBoxPreference) prefSet.findPreference(KEY_VOLUME_ADJUST_SOUNDS_PROP);
        mVolumeAdjustSounds.setPersistent(false);

        mDisableRebootPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_REBOOT_PROP);
        mDisableScreenshotPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_SCREENSHOT_PROP);
        mDisableAirplanePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_AIRPLANE_PROP);
        mDisableRingerPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_RINGER_PROP);
        mDisableTitlePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_TITLE_PROP);

        mCustomCarrierLabel = prefSet.findPreference(CUSTOM_CARRIER_LABEL_PROP);
        mLockscreenTextColor = (ColorPickerPreference) prefSet.findPreference(LOCKSCREEN_TEXT_COLOR_PROP);
        mLockscreenTextColor.setOnPreferenceChangeListener(this);

        updateCenterClockStatusBar();
        updateClockWeekday();
        updateDisableAlarm();

        updateBatteryBar();
        updateBatteryBarStyle();
        updateBatteryBarColor();
        updateBatteryBarChargAnim();
        updateBatteryBarThickness();

        updateDisableBootAnimation();
        updateDisableBootAudio();
        updateDisableBugmailer();
        updateRaisedBrightness();

        updateBackButtonEndsCall();
        updateHomeButtonAnswersCall();
        updateVolumeAdjustSound();

        updateDisableReboot();
        updateDisableScreenshot();
        updateDisableAirplane();
        updateDisableRinger();
        updateDisableTitle();

        updateCustomCarrierLabel();
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
        mCenterClockStatusBar.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, 0) == 1);
    }

    private void updateClockWeekday() {
        mClockWeekday.setValue(Integer.toString(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_WEEKDAY, 0)));
    }

    private void updateDisableAlarm() {
        mDisableAlarmPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_SHOW_ALARM, 1) == 1);
    }

    private void updateBatteryBar() {
        mBatteryBar.setValue((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR, 0)) + "");
    }

    private void updateBatteryBarStyle() {
        mBatteryBarStyle.setValue((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0)) + "");
    }

    private void updateBatteryBarChargAnim() {
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);
    }

    private void updateBatteryBarThickness() {
        mBatteryBarThickness.setValue((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1)) + "");
    }

    private void updateDisableBootAnimation() {
        mDisableBootanimPref.setChecked("1".equals(SystemProperties.get(DISABLE_BOOTANIMATION_PERSIST_PROP, "0")));
        boolean status = mDisableBootanimPref.isChecked();
        if (status) {
            mDisableBootAudioPref.setEnabled(false);
        } else {
            mDisableBootAudioPref.setEnabled(true);
        }
    }

    private void updateDisableBootAudio() {
        if(!new File("/system/media/boot_audio.mp3").exists() && !new File("/system/media/boot_audio.jbmp").exists() ) {
            mDisableBootAudioPref.setEnabled(false);
            mDisableBootAudioPref.setSummary(R.string.pref_disable_bootaudio_summary_disabled);
        } else {
            mDisableBootAudioPref.setChecked(!new File("/system/media/boot_audio.mp3").exists());
        }
    }

    private void updateDisableBugmailer() {
        mDisableBugmailerPref.setChecked(!new File("/system/bin/bugmailer.sh").exists());
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

    private void updateCustomCarrierLabel() {
        mCustomCarrierLabelSummary = Settings.System.getString(getActivity().getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomCarrierLabelSummary == null || mCustomCarrierLabelSummary.length() == 0) {
            mCustomCarrierLabel.setSummary(R.string.pref_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelSummary);
        }
    }


    /* Write functions */
    private void writeCenterClockStatusBar() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, mCenterClockStatusBar.isChecked() ? 1 : 0);
        Helpers.restartSystemUI();
    }

    private void writeClockWeekday(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_WEEKDAY, Integer.parseInt((String) NewVal));
    }

    private void writeDisableAlarm() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_SHOW_ALARM, mDisableAlarmPref.isChecked() ? 1 : 0);
    }

    private void writeClockColorPicker(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        preference.setSummary(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_COLOR, ColorPickerPreference.convertToColorInt(hex));
    }

    private void writeBatteryBar(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR, Integer.parseInt((String) NewVal));
    }

    private void writeBatteryBarStyle(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_STYLE, Integer.parseInt((String) NewVal));
    }

    private void writeBatteryBarColor(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        preference.setSummary(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_COLOR, ColorPickerPreference.convertToColorInt(hex));
    }

    private void writeBatteryBarChargAnim() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, mBatteryBarChargingAnimation.isChecked() ? 1 : 0);
    }

    private void writeBatteryBarThickness(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, Integer.parseInt((String) NewVal));
    }

    private void writeDisableBootAnimation() {
        SystemProperties.set(DISABLE_BOOTANIMATION_PERSIST_PROP, mDisableBootanimPref.isChecked() ? "1" : "0");
        updateDisableBootAnimation();
    }

    private void writeDisableBootAudio() {
        boolean status = mDisableBootAudioPref.isChecked();
        if (status) {
            Helpers.getMount("rw");
            new CMDProcessor().su.runWaitFor("mv /system/media/boot_audio.mp3 /system/media/boot_audio.jbmp");
            Helpers.getMount("ro");
        } else {
            Helpers.getMount("rw");
            new CMDProcessor().su.runWaitFor("mv /system/media/boot_audio.jbmp /system/media/boot_audio.mp3");
            Helpers.getMount("ro");
        }
    }

    private void writeDisableBugmailer() {
        boolean status = mDisableBugmailerPref.isChecked();
        if (status) {
            Helpers.getMount("rw");
            new CMDProcessor().su.runWaitFor("mv /system/bin/bugmailer.sh /system/bin/bugmailer.jbmp");
            Helpers.getMount("ro");
        } else {
            Helpers.getMount("rw");
            new CMDProcessor().su.runWaitFor("mv /system/bin/bugmailer.jbmp /system/bin/bugmailer.sh");
            Helpers.getMount("ro");
        }
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

    private void writeCustomCarrierLabel() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(R.string.pref_carrier_label_title);
        alert.setMessage(R.string.pref_carrier_label_subhead);

                final EditText input = new EditText(getActivity());

                input.setText(mCustomCarrierLabelSummary != null ? mCustomCarrierLabelSummary : "");
                alert.setView(input);
                alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = ((Spannable) input.getText()).toString();
                        Settings.System.putString(getActivity().getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL, value);
                        updateCustomCarrierLabel();
                    }
                });

                alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
            alert.show();
    }

    private void writeLockscreenTextColor(Object NewVal) {
        String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(NewVal)));
        preference.setSummary(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_TEXT_COLOR, ColorPickerPreference.convertToColorInt(hex));
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCenterClockStatusBar) {
            writeCenterClockStatusBar();
        } else if (preference == mDisableAlarmPref) {
            writeDisableAlarm();
        } else if (preference == mBatteryBarChargingAnimation) {
            writeBatteryBarChargAnim();
        } else if (preference == mDisableBootanimPref) {
            writeDisableBootAnimation();
        } else if (preference == mDisableBootAudioPref) {
            writeDisableBootAudio();
        } else if (preference == mDisableBugmailerPref) {
            writeDisableBugmailer();
        } else if (preference == mRaisedBrightnessPref) {
            writeRaisedBrightness();
        } else if (preference == mBackButtonEndsCallPref) {
            writeBackButtonEndsCall();
        } else if (preference == mHomeButtonAnswersCall) {
            writeHomeButtonAnswersCall();
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
        } else if (preference == mCustomCarrierLabel) {
            writeCustomCarrierLabel();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mClockColorPicker) {
            writeClockColorPicker(newValue);
        } else if (preference == mClockWeekday) {
            writeClockWeekday(newValue);
        } else if (preference == mBatteryBarColor) {
            writeBatteryBarColor(newValue);
        } else if (preference == mBatteryBar) {
            writeBatteryBar(newValue);
        } else if (preference == mBatteryBarStyle) {
            writeBatteryBarStyle(newValue);
        } else if (preference == mBatteryBarThickness) {
            writeBatteryBarThickness(newValue);
        } else if (preference == mLockscreenTextColor) {
            writeLockscreenTextColor(newValue);
        }
        return false;
    }
}

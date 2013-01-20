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
    private static final String CLOCK_COLOR_PICKER_PROP = "pref_clock_color";
    private static final String STATUSBAR_TRANSPARENCY_PROP = "pref_statusbar_transparency";

    private static final String BATT_BAR_PROP = "pref_battery_bar";
    private static final String BATT_BAR_STYLE_PROP = "pref_battery_bar_style";
    private static final String BATT_BAR_COLOR_PROP = "pref_battery_bar_color";
    private static final String BATT_BAR_WIDTH_PROP = "pref_battery_bar_thickness";
    private static final String BATT_ANIMATE_PROP = "pref_battery_bar_animate";

    private static final String DISABLE_BOOTANIMATION_PREF = "pref_disable_boot_animation";
    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";
    private static final String DISABLE_BOOTAUDIO_PROP = "pref_disable_bootaudio";
    private static final String DISABLE_BUGMAILER_PROP = "pref_disable_bugmailer";
    private static final String RAISED_BRIGHTNESS_PROP = "pref_raisedbrightness";
    private static final String SHOW_NAVBAR_PROP = "pref_show_navbar";

    private static final String BACK_BUTTON_ENDS_CALL_PROP = "pref_back_button_ends_call";
    private static final String MENU_BUTTON_ANSWERS_CALL_PROP = "pref_menu_button_answers_call";
    private static final String KEY_VOLUME_ADJUST_SOUNDS_PROP = "pref_volume_adjust_sounds";

    private static final String DISABLE_REBOOT_PROP = "pref_disable_reboot";
    private static final String DISABLE_SCREENSHOT_PROP = "pref_disable_screenshot";
    private static final String DISABLE_AIRPLANE_PROP = "pref_disable_airplane";
    private static final String DISABLE_RINGER_PROP = "pref_disable_ringer";
    private static final String DISABLE_TITLE_PROP = "pref_disable_title";

    private static final String CUSTOM_CARRIER_LABEL_PROP = "pref_carrier_label";
    private static final String LOCKSCREEN_TEXT_COLOR_PROP = "pref_lockscreen_text_color";
    private static final String LOCKSCREEN_STYLES_PROP = "pref_lockscreen_styles";
    private static final String ROTARY_ARROWS_PROP = "pref_rotary_arrows";
    private static final String SLIDER_TEXT_PROP = "pref_slider_text";

    private final Configuration mCurrentConfig = new Configuration();

    private CheckBoxPreference mCenterClockStatusBarPref;
    private ListPreference mClockWeekdayPref;
    private ColorPickerPreference mClockColorPicker;
    private ListPreference mStatusbarTransparencyPref;

    private CheckBoxPreference mBatteryBarPref;
    private ListPreference mBatteryBarStylePref;
    private ListPreference mBatteryBarThicknessPref;
    private CheckBoxPreference mBatteryBarChargingAnimationPref;
    private ColorPickerPreference mBatteryBarColor;

    private CheckBoxPreference mDisableBootanimPref;
    private CheckBoxPreference mDisableBootAudioPref;
    private CheckBoxPreference mDisableBugmailerPref;
    private CheckBoxPreference mRaisedBrightnessPref;
    private CheckBoxPreference mShowNavbarPref;

    private CheckBoxPreference mBackButtonEndsCallPref;
    private CheckBoxPreference mMenuButtonAnswersCallPref;
    private CheckBoxPreference mVolumeAdjustSoundsPref;

    private CheckBoxPreference mDisableRebootPref;
    private CheckBoxPreference mDisableScreenshotPref;
    private CheckBoxPreference mDisableAirplanePref;
    private CheckBoxPreference mDisableRingerPref;
    private CheckBoxPreference mDisableTitlePref;

    private Preference mCustomCarrierLabel;
    private String mCustomCarrierLabelSummary = null;
    private ColorPickerPreference mLockscreenTextColor;
    private ListPreference mLockStylePref;
    private CheckBoxPreference mRotaryArrowsPref;
    private CheckBoxPreference mSliderTextPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "\n\nWelcome in JB Mini Project's custom settings!!!\n\n");

        addPreferencesFromResource(R.xml.jbmini_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCenterClockStatusBarPref = (CheckBoxPreference) prefSet.findPreference(CENTER_CLOCK_STATUS_BAR_PROP);
        mClockWeekdayPref = (ListPreference) prefSet.findPreference(CLOCK_WEEKDAY_PROP);
        mClockWeekdayPref.setOnPreferenceChangeListener(this);
        mClockColorPicker = (ColorPickerPreference) prefSet.findPreference(CLOCK_COLOR_PICKER_PROP);
        mClockColorPicker.setOnPreferenceChangeListener(this);
        mStatusbarTransparencyPref = (ListPreference) prefSet.findPreference(STATUSBAR_TRANSPARENCY_PROP);
        mStatusbarTransparencyPref.setOnPreferenceChangeListener(this);

        mBatteryBarPref = (CheckBoxPreference) prefSet.findPreference(BATT_BAR_PROP);
        mBatteryBarStylePref = (ListPreference) prefSet.findPreference(BATT_BAR_STYLE_PROP);
        mBatteryBarStylePref.setOnPreferenceChangeListener(this);
        mBatteryBarColor = (ColorPickerPreference) prefSet.findPreference(BATT_BAR_COLOR_PROP);
        mBatteryBarColor.setOnPreferenceChangeListener(this);
        mBatteryBarChargingAnimationPref = (CheckBoxPreference) findPreference(BATT_ANIMATE_PROP);
        mBatteryBarThicknessPref = (ListPreference) prefSet.findPreference(BATT_BAR_WIDTH_PROP);
        mBatteryBarThicknessPref.setOnPreferenceChangeListener(this);

        mDisableBootanimPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTANIMATION_PREF);
        mDisableBootAudioPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTAUDIO_PROP);
        mDisableBugmailerPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BUGMAILER_PROP);
        mRaisedBrightnessPref = (CheckBoxPreference) prefSet.findPreference(RAISED_BRIGHTNESS_PROP);
        mShowNavbarPref = (CheckBoxPreference) prefSet.findPreference(SHOW_NAVBAR_PROP);

        mBackButtonEndsCallPref = (CheckBoxPreference) prefSet.findPreference(BACK_BUTTON_ENDS_CALL_PROP);
        mMenuButtonAnswersCallPref = (CheckBoxPreference) prefSet.findPreference(MENU_BUTTON_ANSWERS_CALL_PROP);
        mVolumeAdjustSoundsPref = (CheckBoxPreference) prefSet.findPreference(KEY_VOLUME_ADJUST_SOUNDS_PROP);
        mVolumeAdjustSoundsPref.setPersistent(false);

        mDisableRebootPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_REBOOT_PROP);
        mDisableScreenshotPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_SCREENSHOT_PROP);
        mDisableAirplanePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_AIRPLANE_PROP);
        mDisableRingerPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_RINGER_PROP);
        mDisableTitlePref = (CheckBoxPreference) prefSet.findPreference(DISABLE_TITLE_PROP);

        mCustomCarrierLabel = prefSet.findPreference(CUSTOM_CARRIER_LABEL_PROP);
        mLockscreenTextColor = (ColorPickerPreference) prefSet.findPreference(LOCKSCREEN_TEXT_COLOR_PROP);
        mLockscreenTextColor.setOnPreferenceChangeListener(this);
        mLockStylePref = (ListPreference) prefSet.findPreference(LOCKSCREEN_STYLES_PROP);
        mLockStylePref.setOnPreferenceChangeListener(this);
        mRotaryArrowsPref = (CheckBoxPreference) prefSet.findPreference(ROTARY_ARROWS_PROP);
        mSliderTextPref = (CheckBoxPreference) prefSet.findPreference(SLIDER_TEXT_PROP);


        updateCenterClockStatusBar();
        updateClockWeekday();
        updateStatusbarTransparency();

        updateBatteryBar();
        updateBatteryBarStyle();
        updateBatteryBarChargAnim();
        updateBatteryBarThickness();

        updateDisableBootAnimation();
        updateDisableBootAudio();
        updateDisableBugmailer();
        updateRaisedBrightness();
        updateShowNavBar();

        updateBackButtonEndsCall();
        updateMenuButtonAnswersCall();
        updateVolumeAdjustSound();

        updateDisableReboot();
        updateDisableScreenshot();
        updateDisableAirplane();
        updateDisableRinger();
        updateDisableTitle();

        updateCustomCarrierLabel();
        updateLockscreenStyle();
        updateLockscreenArrows();
        updateLockscreenSliderT();
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
        mRaisedBrightnessPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_RAISED_BRIGHTNESS, 0) == 1);
    }

    private void updateShowNavBar() {
        mShowNavbarPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SHOW_NAVBAR, 0) == 1);
    }

    private void updateBackButtonEndsCall() {
        final int incallBackBehavior = Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.INCALL_BACK_BUTTON_BEHAVIOR,
            Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_DEFAULT);
        final boolean backButtonEndsCall =
            (incallBackBehavior == Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_HANGUP);
        mBackButtonEndsCallPref.setChecked(backButtonEndsCall);
    }

    private void updateMenuButtonAnswersCall() {
        final int incallMenuBehavior = Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.RING_MENU_BUTTON_BEHAVIOR,
            Settings.System.RING_MENU_BUTTON_BEHAVIOR_DEFAULT);
        final boolean menuButtonAnswersCall =
            (incallMenuBehavior == Settings.System.RING_MENU_BUTTON_BEHAVIOR_ANSWER);
        mMenuButtonAnswersCallPref.setChecked(menuButtonAnswersCall);
    }

    private void updateVolumeAdjustSound() {
        mVolumeAdjustSoundsPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, 1) != 0);
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

    private void updateLockscreenStyle() {
        mLockStylePref.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_STYLE, 0) + "");
        mLockStylePref.setOnPreferenceChangeListener(this);
        int stylenum = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_STYLE, 0);
        if (stylenum == 0) {
            mRotaryArrowsPref.setEnabled(false);
            mSliderTextPref.setEnabled(false);
        }
        else if (stylenum == 1) {
            mRotaryArrowsPref.setEnabled(false);
            mSliderTextPref.setEnabled(true);
        }
        else if (stylenum == 2) {
            mRotaryArrowsPref.setEnabled(true);
            mSliderTextPref.setEnabled(false);
        }
    }

    private void updateLockscreenArrows() {
        mRotaryArrowsPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_ARROWS, 0) == 1);
    }

    private void updateLockscreenSliderT() {
        mSliderTextPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_HINT, 0) == 1);
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
    }

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
        File f = new File("/sys/devices/platform/i2c-adapter/i2c-0/0-0036/mode");
        String modeFile = "";

        if (f.isFile() && f.canRead())
            modeFile = "/sys/devices/platform/i2c-adapter/i2c-0/0-0036/mode";
        else
            modeFile = "/sys/devices/i2c-0/0-0036/mode";

        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_RAISED_BRIGHTNESS, mRaisedBrightnessPref.isChecked() ? 1 : 0);
        Utils.fileWriteOneLine(modeFile, mRaisedBrightnessPref.isChecked() ? "i2c_pwm" : "i2c_pwm_als");
    }

    private void writeShowNavBar() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SHOW_NAVBAR, mShowNavbarPref.isChecked() ? 1 : 0);
    }

    private void writeBackButtonEndsCall() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.INCALL_BACK_BUTTON_BEHAVIOR, mBackButtonEndsCallPref.isChecked() ? Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_HANGUP : Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_BACK);
    }

    private void writeMenuButtonAnswersCall() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.RING_MENU_BUTTON_BEHAVIOR, mMenuButtonAnswersCallPref.isChecked() ? Settings.System.RING_MENU_BUTTON_BEHAVIOR_ANSWER : Settings.System.RING_MENU_BUTTON_BEHAVIOR_DO_NOTHING);
    }

    private void writeVolumeAdjustSound() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, mVolumeAdjustSoundsPref.isChecked() ? 1 : 0);
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
        mLockscreenTextColor.setSummary(hex);
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_TEXT_COLOR, ColorPickerPreference.convertToColorInt(hex));
        mLockscreenTextColor.setOnPreferenceChangeListener(this);
    }

    private void writeLockscreenStyle(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_STYLE, Integer.parseInt((String) NewVal));
        updateLockscreenStyle();
    }

    private void writeLockscreenArrows() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_ARROWS, mRotaryArrowsPref.isChecked() ? 1 : 0);
    }

    private void writeLockscreenSliderT() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_HINT, mSliderTextPref.isChecked() ? 1 : 0);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCenterClockStatusBarPref) {
            writeCenterClockStatusBar();
        } else if (preference == mBatteryBarPref) {
            writeBatteryBar();
        } else if (preference == mBatteryBarChargingAnimationPref) {
            writeBatteryBarChargAnim();
        } else if (preference == mDisableBootanimPref) {
            writeDisableBootAnimation();
        } else if (preference == mDisableBootAudioPref) {
            writeDisableBootAudio();
        } else if (preference == mDisableBugmailerPref) {
            writeDisableBugmailer();
        } else if (preference == mRaisedBrightnessPref) {
            writeRaisedBrightness();
        } else if (preference == mShowNavbarPref) {
            writeShowNavBar();
        } else if (preference == mBackButtonEndsCallPref) {
            writeBackButtonEndsCall();
        } else if (preference == mMenuButtonAnswersCallPref) {
            writeMenuButtonAnswersCall();
        } else if (preference == mVolumeAdjustSoundsPref) {
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
        } else if (preference == mRotaryArrowsPref) {
            writeLockscreenArrows();
        } else if (preference == mSliderTextPref) {
            writeLockscreenSliderT();
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
        } else if (preference == mBatteryBarColor) {
            writeBatteryBarColor(newValue);
        } else if (preference == mBatteryBarStylePref) {
            writeBatteryBarStyle(newValue);
        } else if (preference == mBatteryBarThicknessPref) {
            writeBatteryBarThickness(newValue);
        } else if (preference == mLockscreenTextColor) {
            writeLockscreenTextColor(newValue);
        } else if (preference == mLockStylePref) {
            writeLockscreenStyle(newValue);
        }
        return false;
    }
}

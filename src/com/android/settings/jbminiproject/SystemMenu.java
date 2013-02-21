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

public class SystemMenu extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "JBMP_Settings";
    private static final boolean DEBUG = true;

    private static final String DISABLE_BOOTANIMATION_PREF = "pref_disable_boot_animation";
    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";
    private static final String DISABLE_BOOTAUDIO_PROP = "pref_disable_bootaudio";
    private static final String DISABLE_BUGMAILER_PROP = "pref_disable_bugmailer";
    private static final String RAISED_BRIGHTNESS_PROP = "pref_raisedbrightness";
    private static final String SHOW_NAVBAR_PROP = "pref_show_navbar";
    private static final String KEY_VOLUME_ADJUST_SOUNDS_PROP = "pref_volume_adjust_sounds";

    private CheckBoxPreference mDisableBootanimPref;
    private CheckBoxPreference mDisableBootAudioPref;
    private CheckBoxPreference mDisableBugmailerPref;
    private CheckBoxPreference mRaisedBrightnessPref;
    private CheckBoxPreference mShowNavbarPref;
    private CheckBoxPreference mVolumeAdjustSoundsPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_system);

        PreferenceScreen prefSet = getPreferenceScreen();

        mDisableBootanimPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTANIMATION_PREF);
        mDisableBootAudioPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTAUDIO_PROP);
        mDisableBugmailerPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BUGMAILER_PROP);
        mRaisedBrightnessPref = (CheckBoxPreference) prefSet.findPreference(RAISED_BRIGHTNESS_PROP);
        mShowNavbarPref = (CheckBoxPreference) prefSet.findPreference(SHOW_NAVBAR_PROP);
        mVolumeAdjustSoundsPref = (CheckBoxPreference) prefSet.findPreference(KEY_VOLUME_ADJUST_SOUNDS_PROP);
        mVolumeAdjustSoundsPref.setPersistent(false);


        updateDisableBootAnimation();
        updateDisableBootAudio();
        updateDisableBugmailer();
        updateRaisedBrightness();
        updateShowNavBar();
        updateVolumeAdjustSound();
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

    private void updateVolumeAdjustSound() {
        mVolumeAdjustSoundsPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, 1) != 0);
    }


    /* Write functions */
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

    private void writeVolumeAdjustSound() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, mVolumeAdjustSoundsPref.isChecked() ? 1 : 0);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mDisableBootanimPref) {
            writeDisableBootAnimation();
        } else if (preference == mDisableBootAudioPref) {
            writeDisableBootAudio();
        } else if (preference == mDisableBugmailerPref) {
            writeDisableBugmailer();
        } else if (preference == mRaisedBrightnessPref) {
            writeRaisedBrightness();
        } else if (preference == mShowNavbarPref) {
            writeShowNavBar();
        } else if (preference == mVolumeAdjustSoundsPref) {
            writeVolumeAdjustSound();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}

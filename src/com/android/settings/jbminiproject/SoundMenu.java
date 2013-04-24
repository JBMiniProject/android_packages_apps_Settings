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

import android.app.AlertDialog;
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
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.os.Handler;
import android.util.Log;
import android.net.Uri;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.IPowerManager;

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

public class SoundMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "JBMP_Settings";
    private static final boolean DEBUG = true;

    private static final String KEY_VOLUME_ADJUST_SOUNDS_PROP = "pref_volume_adjust_sounds";
    private static final String KEY_SWAP_VOLUME_BUTTONS_PROP = "pref_swap_volume_buttons";
    private static final String CAMERA_SHUTTER_MUTE_PROP = "pref_camera-mute";
    private static final String CAMERA_SHUTTER_DISABLE_PROP = "persist.sys.camera-mute";

    private SwitchPreference mVolumeAdjustSoundsPref;
    private SwitchPreference mSwapVolumeButtonsPref;
    private SwitchPreference mDisableCameraSoundPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_sound);

        PreferenceScreen prefSet = getPreferenceScreen();

        mVolumeAdjustSoundsPref = (SwitchPreference) prefSet.findPreference(KEY_VOLUME_ADJUST_SOUNDS_PROP);
        mVolumeAdjustSoundsPref.setPersistent(false);
        mSwapVolumeButtonsPref = (SwitchPreference) prefSet.findPreference(KEY_SWAP_VOLUME_BUTTONS_PROP);
        mDisableCameraSoundPref = (SwitchPreference) prefSet.findPreference(CAMERA_SHUTTER_MUTE_PROP);


        updateVolumeAdjustSound();
        updateSwapVolumeButtons();
        updateDisableCameraSound();
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
    private void updateVolumeAdjustSound() {
        mVolumeAdjustSoundsPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, 1) != 0);
        mVolumeAdjustSoundsPref.setOnPreferenceChangeListener(this);
    }

    private void updateSwapVolumeButtons() {
        mSwapVolumeButtonsPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SWAP_VOLUME_KEYS_BY_ROTATE, 0) == 1);
        mSwapVolumeButtonsPref.setOnPreferenceChangeListener(this);
    }

    private void updateDisableCameraSound() {
        mDisableCameraSoundPref.setChecked(SystemProperties.getInt(CAMERA_SHUTTER_DISABLE_PROP, 0) != 0);
        mDisableCameraSoundPref.setOnPreferenceChangeListener(this);
    }


    /* Write functions */
    private void writeVolumeAdjustSound(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, (Boolean) NewVal ? 1 : 0);
    }

    private void writeSwapVolumeButtons(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SWAP_VOLUME_KEYS_BY_ROTATE, (Boolean) NewVal ? 1 : 0);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mVolumeAdjustSoundsPref) {
            writeVolumeAdjustSound(newValue);
            return true;
        } else if (preference == mSwapVolumeButtonsPref) {
            writeSwapVolumeButtons(newValue);
            return true;
        } else if (preference == mDisableCameraSoundPref) {
            if ((Boolean) newValue) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.pref_sound_camera_shutter_disable_warning_title);
                builder.setMessage(R.string.pref_sound_camera_shutter_disable_warning);
                builder.setPositiveButton(com.android.internal.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SystemProperties.set(CAMERA_SHUTTER_DISABLE_PROP, "1");
                        }
                    });

                final SwitchPreference p = (SwitchPreference) preference;
                builder.setNegativeButton(com.android.internal.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            p.setChecked(false);
                        }
                    });

                builder.show();
            } else{
                SystemProperties.set(CAMERA_SHUTTER_DISABLE_PROP, "0");
            }
            return true;
        }
        return false;
    }
}

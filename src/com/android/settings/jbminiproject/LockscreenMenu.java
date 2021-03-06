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
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.os.Handler;
import android.util.Log;
import android.net.Uri;
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

public class LockscreenMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String CUSTOM_CARRIER_LABEL_PROP = "pref_carrier_label";
    private static final String LOCKSCREEN_TEXT_COLOR_PROP = "pref_lockscreen_text_color";
    private static final String LOCKSCREEN_STYLES_PROP = "pref_lockscreen_styles";
    private static final String ROTARY_ARROWS_PROP = "pref_rotary_arrows";
    private static final String SLIDER_TEXT_PROP = "pref_slider_text";

    private static final int DEFAULT_COLOR = 0xffffffff;

    private Preference mCustomCarrierLabel;
    private String mCustomCarrierLabelSummary = null;
    private ColorPickerPreference mLockscreenTextColor;
    private ListPreference mLockStylePref;
    private SwitchPreference mRotaryArrowsPref;
    private SwitchPreference mSliderTextPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_lockscreen);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCustomCarrierLabel = prefSet.findPreference(CUSTOM_CARRIER_LABEL_PROP);
        mLockscreenTextColor = (ColorPickerPreference) prefSet.findPreference(LOCKSCREEN_TEXT_COLOR_PROP);
        mLockscreenTextColor.setOnPreferenceChangeListener(this);
        mLockStylePref = (ListPreference) prefSet.findPreference(LOCKSCREEN_STYLES_PROP);
        mLockStylePref.setOnPreferenceChangeListener(this);
        mRotaryArrowsPref = (SwitchPreference) prefSet.findPreference(ROTARY_ARROWS_PROP);
        mSliderTextPref = (SwitchPreference) prefSet.findPreference(SLIDER_TEXT_PROP);


        updateCustomCarrierLabel();
        updateLockscreenTextColor();
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
    private void updateCustomCarrierLabel() {
        mCustomCarrierLabelSummary = Settings.System.getString(getActivity().getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomCarrierLabelSummary == null || mCustomCarrierLabelSummary.length() == 0) {
            mCustomCarrierLabel.setSummary(R.string.pref_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelSummary);
        }
    }

    private void updateLockscreenTextColor() {
        int intColor;
        intColor = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_TEXT_COLOR, DEFAULT_COLOR);
        String hexColor;
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenTextColor.setSummary(hexColor);
        mLockscreenTextColor.setNewPreviewColor(intColor);
    }

    private void updateLockscreenStyle() {
        mLockStylePref.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_STYLE, 0) + "");
        mLockStylePref.setOnPreferenceChangeListener(this);
        int stylenum = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_STYLE, 0);
        if (stylenum == 0 || stylenum == 3 || stylenum == 4 || stylenum == 5 || stylenum == 6 || stylenum == 7 || stylenum == 8) {
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
        mRotaryArrowsPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_ARROWS, 0) == 0);
        mRotaryArrowsPref.setOnPreferenceChangeListener(this);
    }

    private void updateLockscreenSliderT() {
        mSliderTextPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_HINT, 0) == 0);
        mSliderTextPref.setOnPreferenceChangeListener(this);
    }


    /* Write functions */
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

    private void writeLockscreenArrows(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_ARROWS, (Boolean) NewVal ? 0 : 1);
    }

    private void writeLockscreenSliderT(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_HIDE_HINT, (Boolean) NewVal ? 0 : 1);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCustomCarrierLabel) {
            writeCustomCarrierLabel();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenTextColor) {
            writeLockscreenTextColor(newValue);
            return true;
        } else if (preference == mLockStylePref) {
            writeLockscreenStyle(newValue);
            return true;
        } else if (preference == mRotaryArrowsPref) {
            writeLockscreenArrows(newValue);
            return true;
        } else if (preference == mSliderTextPref) {
            writeLockscreenSliderT(newValue);
            return true;
        }
        return false;
    }
}

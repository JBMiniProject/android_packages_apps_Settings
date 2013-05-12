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
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.io.InputStream;
import java.io.IOException;

public class CallMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String BACK_BUTTON_ENDS_CALL_PROP = "pref_back_button_ends_call";
    private static final String MENU_BUTTON_ANSWERS_CALL_PROP = "pref_menu_button_answers_call";
    private static final String PICK_UP_TO_CALL_PROP = "pref_pick_up_to_call";
    private static final String NATURAL_MOTION_TO_ANSWER_PROP = "pref_natural_motion";

    private SwitchPreference mBackButtonEndsCallPref;
    private SwitchPreference mMenuButtonAnswersCallPref;
    private SwitchPreference mPickUpToCallPref;
    private SwitchPreference mNaturalMotionAnswerPref;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jbmini_call);

        PreferenceScreen prefSet = getPreferenceScreen();

        mBackButtonEndsCallPref = (SwitchPreference) prefSet.findPreference(BACK_BUTTON_ENDS_CALL_PROP);
        mMenuButtonAnswersCallPref = (SwitchPreference) prefSet.findPreference(MENU_BUTTON_ANSWERS_CALL_PROP);
        mPickUpToCallPref = (SwitchPreference) prefSet.findPreference(PICK_UP_TO_CALL_PROP);
        mNaturalMotionAnswerPref = (SwitchPreference) prefSet.findPreference(NATURAL_MOTION_TO_ANSWER_PROP);

        updateBackButtonEndsCall();
        updateMenuButtonAnswersCall();
        updatePickUpToCall();
        updateNaturalMotionAnswer();
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
    private void updateBackButtonEndsCall() {
        final int incallBackBehavior = Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.INCALL_BACK_BUTTON_BEHAVIOR,
            Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_DEFAULT);
        final boolean backButtonEndsCall =
            (incallBackBehavior == Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_HANGUP);
        mBackButtonEndsCallPref.setChecked(backButtonEndsCall);
        mBackButtonEndsCallPref.setOnPreferenceChangeListener(this);
    }

    private void updateMenuButtonAnswersCall() {
        final int incallMenuBehavior = Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.RING_MENU_BUTTON_BEHAVIOR,
            Settings.System.RING_MENU_BUTTON_BEHAVIOR_DEFAULT);
        final boolean menuButtonAnswersCall =
            (incallMenuBehavior == Settings.System.RING_MENU_BUTTON_BEHAVIOR_ANSWER);
        mMenuButtonAnswersCallPref.setChecked(menuButtonAnswersCall);
        mMenuButtonAnswersCallPref.setOnPreferenceChangeListener(this);
    }

    private void updatePickUpToCall() {
        mPickUpToCallPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.PICK_UP_TO_CALL, 0) == 1);
        mPickUpToCallPref.setOnPreferenceChangeListener(this);
    }

    private void updateNaturalMotionAnswer() {
        mNaturalMotionAnswerPref.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.NATURAL_MOTION, 0) == 1);
        mNaturalMotionAnswerPref.setOnPreferenceChangeListener(this);
    }


    /* Write functions */
    private void writeBackButtonEndsCall(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.INCALL_BACK_BUTTON_BEHAVIOR, (Boolean) NewVal ? Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_HANGUP : Settings.System.INCALL_BACK_BUTTON_BEHAVIOR_BACK);
    }

    private void writeMenuButtonAnswersCall(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.RING_MENU_BUTTON_BEHAVIOR, (Boolean) NewVal ? Settings.System.RING_MENU_BUTTON_BEHAVIOR_ANSWER : Settings.System.RING_MENU_BUTTON_BEHAVIOR_DO_NOTHING);
    }

    private void writePickUpToCall(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.PICK_UP_TO_CALL, (Boolean) NewVal ? 1 : 0);
    }

    private void writeNaturalMotionAnswer(Object NewVal) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.NATURAL_MOTION, (Boolean) NewVal ? 1 : 0);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mBackButtonEndsCallPref) {
            writeBackButtonEndsCall(value);
            return true;
        } else if (preference == mMenuButtonAnswersCallPref) {
            writeMenuButtonAnswersCall(value);
            return true;
        } else if (preference == mPickUpToCallPref) {
            writePickUpToCall(value);
            return true;
        } else if (preference == mNaturalMotionAnswerPref) {
            writeNaturalMotionAnswer(value);
            return true;
        }
        return false;
    }
}

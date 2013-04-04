/*
 * Copyright (C) 2013 JB Mini Project
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

import com.android.settings.R;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.ContextThemeWrapper;
import android.app.Activity;
import android.app.ListActivity;
import android.preference.PreferenceActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import android.provider.Settings;
import com.android.settings.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class About extends PreferenceActivity {

    public static final String TAG = "About";

    Preference mSiteUrl;
    Preference mSourceUrl;
    Preference mIrcUrl;
    Preference mVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_about);
        mSiteUrl = findPreference("jbmp_website");
        mSourceUrl = findPreference("jbmp_source");
        mIrcUrl = findPreference("jbmp_irc");
        mVersion = findPreference("mod_version");

        PreferenceGroup devsGroup = (PreferenceGroup) findPreference("devs");
        ArrayList<Preference> devs = new ArrayList<Preference>();
        for (int i = 0; i < devsGroup.getPreferenceCount(); i++) {
            devs.add(devsGroup.getPreference(i));
        }
        devsGroup.removeAll();
        devsGroup.setOrderingAsAdded(false);
        Collections.shuffle(devs);
        for(int i = 0; i < devs.size(); i++) {
            Preference p = devs.get(i);
            p.setOrder(i);

            devsGroup.addPreference(p);
        }

        mVersion.setSummary(
            SystemProperties.get("ro.modversion", getResources().getString(R.string.jbmp_version_summary)));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSiteUrl) {
            launchUrl("http://jbminiproject.weebly.com/");
        } else if (preference == mSourceUrl) {
            launchUrl("http://github.com/JBMiniProject");
        } else if (preference == mIrcUrl) {
            launchUrl("http://webchat.freenode.net/?channels=JBMP");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent donate = new Intent(Intent.ACTION_VIEW, uriUrl);
        this.startActivity(donate);
    }
}

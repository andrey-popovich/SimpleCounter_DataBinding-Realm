package com.example.andrey.p_databindingsample;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;


import io.realm.Realm;

public class SettingsActivity extends PreferenceActivity{

    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        Preference removeAll = getPreferenceManager().findPreference("removeCounters");
        if (removeAll != null) {
            removeAll.setOnPreferenceClickListener(preference -> {
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                return true;
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {realm.close();}
    }
}
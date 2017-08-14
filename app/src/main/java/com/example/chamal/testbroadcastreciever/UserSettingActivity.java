package com.example.chamal.testbroadcastreciever;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Chamal on 11/16/2016.
 */

public class UserSettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}

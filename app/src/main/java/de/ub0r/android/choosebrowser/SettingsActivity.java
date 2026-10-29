package de.ub0r.android.choosebrowser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

@SuppressLint("ExportedPreferenceActivity")
public class SettingsActivity extends AppCompatPreferenceActivity {

    static final String PREF_NAME_REMEMBER_BY_DEFAULT = "remember_by_default";
    static final Boolean PREF_DEFAULT_REMEMBER_BY_DEFAULT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setSubtitle(R.string.settings);
        //noinspection deprecation
        addPreferencesFromResource(R.xml.pref_general);
        EdgeToEdge.applyInsets(this);
        // set in code: a hardcoded targetPackage would break the .alpha build type
        //noinspection deprecation
        findPreference("preferred_apps").setIntent(new Intent(this, PreferredAppsActivity.class));
        //noinspection deprecation
        findPreference("hidden_apps").setIntent(new Intent(this, HiddenAppsActivity.class));
    }
}

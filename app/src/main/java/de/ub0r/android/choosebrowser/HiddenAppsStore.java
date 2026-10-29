package de.ub0r.android.choosebrowser;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

class HiddenAppsStore {

    private static final String PREFS_NAME = "hidden_apps";
    private static final String KEY_PACKAGES = "packages";

    private final SharedPreferences mPrefs;

    HiddenAppsStore(@NonNull final Context context) {
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    Set<String> list() {
        // copy, the set returned by SharedPreferences must not be modified
        return new HashSet<>(mPrefs.getStringSet(KEY_PACKAGES, new HashSet<String>()));
    }

    void setHidden(@NonNull final String packageName, final boolean hidden) {
        final Set<String> packages = list();
        if (hidden) {
            packages.add(packageName);
        } else {
            packages.remove(packageName);
        }
        mPrefs.edit().putStringSet(KEY_PACKAGES, packages).apply();
    }
}

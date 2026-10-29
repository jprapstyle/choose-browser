package de.ub0r.android.choosebrowser;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.ActivityNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.Future;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.ub0r.android.logg0r.Log;

public class ChooserFragment extends AppCompatDialogFragment {

    private static final String TAG = "ChooserFragment";
    private static final String EXTRA_URI = "uri";
    private static final String EXTRA_SHOW_AS_DIALOG = "showAsDialog";

    private CheckedTextView mRememberPreference;
    private final Handler main = new Handler(Looper.getMainLooper());
    private Future<?> pending;
    private IntentParser resolver;
    private Uri currentUri;
    private int generation;
    private boolean resumed;
    private boolean launching;
    private Boolean restoredRemember;
    private boolean preferredAttempted;


    static ChooserFragment newInstance(@NonNull final Uri uri, final boolean showAsDialog) {
        ChooserFragment f = new ChooserFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_URI, uri.toString());
        args.putBoolean(EXTRA_SHOW_AS_DIALOG, showAsDialog);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            restoredRemember = savedInstanceState.getBoolean("remember");
            preferredAttempted = savedInstanceState.getBoolean("preferredAttempted");
        }

        if (getArguments().getBoolean(EXTRA_SHOW_AS_DIALOG)) {
            setStyle(DialogFragment.STYLE_NORMAL, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chooser, container, false);
    }

    @Override
    public void onViewCreated(final View container, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(container, savedInstanceState);
        showChooser(container);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.activity_chooser, menu);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            case R.id.menu_hidden_apps:
                startActivity(new Intent(getContext(), HiddenAppsActivity.class));
                return true;
            case R.id.menu_preferred_apps:
                startActivity(new Intent(getContext(), PreferredAppsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void finish() {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            // remove the chooser task so it does not linger in the recent apps list
            activity.finishAndRemoveTask();
        }
    }

    private void setTitle(@StringRes final int title) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setTitle(title);
        }
        final Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setTitle(title);
        }
    }

    private void setSubtitle(final String subtitle) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            final AppCompatActivity compatActivity = (AppCompatActivity) activity;
            if (compatActivity.getSupportActionBar() != null) {
                compatActivity.getSupportActionBar().setSubtitle(subtitle);
            }
        }
        final Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setTitle(String.format("%s - %s", getString(R.string.app_name), subtitle));
        }
    }

    private void showChooser(final View container) {
        currentUri = Uri.parse(getArguments().getString(EXTRA_URI));
        setTitle(R.string.app_name);
        setSubtitle(currentUri.toString());
        mRememberPreference = container.findViewById(R.id.remember_preference);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        mRememberPreference.setChecked(restoredRemember != null ? restoredRemember : prefs.getBoolean(
                SettingsActivity.PREF_NAME_REMEMBER_BY_DEFAULT, SettingsActivity.PREF_DEFAULT_REMEMBER_BY_DEFAULT));
        mRememberPreference.setOnClickListener(view -> mRememberPreference.toggle());
        RecyclerView list = container.findViewById(android.R.id.list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadChooser(container, !preferredAttempted);
    }

    private void loadChooser(View container, boolean checkPreferred) {
        cancelLoad();
        launching = false;
        final int request = generation;
        final Uri input = currentUri;
        final Context app = requireContext().getApplicationContext();
        final IntentParser parser = new IntentParser();
        resolver = parser;
        final RecyclerView list = container.findViewById(android.R.id.list);
        final TextView status = container.findViewById(R.id.chooser_status);
        list.setVisibility(View.GONE);
        status.setText(R.string.chooser_loading);
        status.setVisibility(View.VISIBLE);
        final long started = SystemClock.elapsedRealtime();
        pending = BrowserCatalog.WORKERS.submit(() -> {
            try {
                final Uri resolved = parser.resolveRedirect(input);
                if (Thread.currentThread().isInterrupted()) return;
                final ComponentName preferred = checkPreferred ? new PreferredAppsStore(app).get(resolved) : null;
                final List<BrowserCatalog.Item> browsers = BrowserCatalog.load(app, resolved);
                main.post(() -> {
                    if (request != generation || getView() != container || !isAdded()) return;
                    currentUri = resolved;
                    setSubtitle(resolved.toString());
                    if (checkPreferred) {
                        preferredAttempted = true;
                        if (preferred != null && !app.getPackageName().equals(preferred.getPackageName())) {
                            if (launch(resolved, preferred, false)) return;
                            new PreferredAppsStore(app).remove(resolved);
                        }
                    }
                    list.setAdapter(new ChooserAdapter(requireContext(), component ->
                            launch(currentUri, component, mRememberPreference.isChecked()), browsers));
                    status.setText(R.string.chooser_empty);
                    status.setVisibility(browsers.isEmpty() ? View.VISIBLE : View.GONE);
                    list.setVisibility(browsers.isEmpty() ? View.GONE : View.VISIBLE);
                    android.util.Log.i("ChooserPerf", "List ready in " +
                            (SystemClock.elapsedRealtime() - started) + "ms, rows=" + browsers.size());
                });
            } catch (RuntimeException error) {
                android.util.Log.e("ChooserPerf", "Unable to load apps", error);
                main.post(() -> {
                    if (request == generation && getView() == container && isAdded())
                        status.setText(R.string.chooser_load_failed);
                });
            }
        });
    }

    private boolean launch(Uri uri, ComponentName component, boolean remember) {
        if (launching) return false;
        launching = true;
        Intent intent = new Intent(Intent.ACTION_VIEW, uri).setComponent(component);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            if (remember) new PreferredAppsStore(requireContext()).put(uri, component);
            finish();
            return true;
        } catch (ActivityNotFoundException | SecurityException error) {
            launching = false;
            Toast.makeText(requireContext(), R.string.error_app_not_found, Toast.LENGTH_LONG).show();
            if (getView() != null) main.post(() -> {
                if (getView() != null) loadChooser(getView(), false);
            });
            return false;
        }
    }

    @Override public void onResume() {
        super.onResume();
        if (resumed && getView() != null) loadChooser(getView(), false);
        resumed = true;
    }

    @Override public void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean("remember", mRememberPreference != null && mRememberPreference.isChecked());
        state.putBoolean("preferredAttempted", preferredAttempted);
    }

    private void cancelLoad() {
        generation++;
        if (resolver != null) resolver.cancel();
        if (pending != null) pending.cancel(true);
        main.removeCallbacksAndMessages(null);
    }

    @Override public void onDestroyView() {
        cancelLoad();
        mRememberPreference = null;
        resumed = false;
        super.onDestroyView();
    }
}

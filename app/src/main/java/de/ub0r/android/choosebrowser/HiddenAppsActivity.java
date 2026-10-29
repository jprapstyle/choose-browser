package de.ub0r.android.choosebrowser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HiddenAppsActivity extends AppCompatActivity {
    private final Handler main = new Handler(Looper.getMainLooper());
    private Future<?> pending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_apps);
        EdgeToEdge.applyInsets(this);

        //noinspection ConstantConditions
        getSupportActionBar().setSubtitle(R.string.hidden_apps);

        final RecyclerView list = findViewById(android.R.id.list);
        final View emptyListView = findViewById(R.id.empty_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        emptyListView.setVisibility(View.GONE);
        pending = BrowserCatalog.WORKERS.submit(() -> {
            final List<BrowserCatalog.Item> browsers = BrowserCatalog.prepare(getApplicationContext(), listBrowsers());
            main.post(() -> {
                if (isFinishing() || isDestroyed()) return;
                list.setAdapter(new HiddenAppsAdapter(this, new HiddenAppsStore(this), browsers));
                list.setVisibility(browsers.isEmpty() ? View.GONE : View.VISIBLE);
                emptyListView.setVisibility(browsers.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override protected void onDestroy() {
        if (pending != null) pending.cancel(true);
        main.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private List<ResolveInfo> listBrowsers() {
        final PackageManager pm = getPackageManager();
        final String myPackageName = getPackageName();
        final Set<String> seen = new HashSet<>();
        final List<ResolveInfo> browsers = new ArrayList<>();
        for (final String scheme : new String[]{"http", "https"}) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme + "://example.com"));
            for (final ResolveInfo info : pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)) {
                final String packageName = info.activityInfo.packageName;
                if (!myPackageName.equals(packageName) && seen.add(packageName)) {
                    browsers.add(info);
                }
            }
        }
        return browsers;
    }
}

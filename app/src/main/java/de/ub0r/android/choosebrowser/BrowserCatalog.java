package de.ub0r.android.choosebrowser;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Prepare complete rows on workers, so binding never loads package resources. */
final class BrowserCatalog {
    static final ExecutorService WORKERS = Executors.newFixedThreadPool(2);
    private static final LruCache<String, Metadata> CACHE = new LruCache<>(48);
    private static boolean observing;

    static final class Item {
        final ResolveInfo info;
        final CharSequence label;
        final Drawable icon;
        Item(ResolveInfo info, CharSequence label, Drawable icon) {
            this.info = info; this.label = label; this.icon = icon;
        }
        ComponentName component() { return new ComponentName(info.activityInfo.packageName, info.activityInfo.name); }
    }

    private static final class Metadata {
        final CharSequence label;
        final Drawable.ConstantState icon;
        Metadata(CharSequence label, Drawable.ConstantState icon) { this.label = label; this.icon = icon; }
    }

    private static synchronized void observe(Context context) {
        if (observing) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context c, Intent intent) { CACHE.evictAll(); }
        };
        if (Build.VERSION.SDK_INT >= 33) context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else context.registerReceiver(receiver, filter);
        observing = true;
    }

    static List<ResolveInfo> filter(List<ResolveInfo> candidates, String ownPackage, Set<String> hidden) {
        List<ResolveInfo> all = new ArrayList<>();
        List<ResolveInfo> visible = new ArrayList<>();
        for (ResolveInfo info : candidates) {
            if (info.activityInfo == null || ownPackage.equals(info.activityInfo.packageName)) continue;
            if (!info.activityInfo.exported || !info.activityInfo.enabled || !info.activityInfo.applicationInfo.enabled) continue;
            all.add(info);
            if (!hidden.contains(info.activityInfo.packageName)) visible.add(info);
        }
        // Apply the existing all-hidden fallback after excluding this chooser itself.
        return visible.isEmpty() ? all : visible;
    }

    static List<Item> load(Context context, Uri uri) {
        Context app = context.getApplicationContext();
        List<ResolveInfo> infos = app.getPackageManager().queryIntentActivities(
                new Intent(Intent.ACTION_VIEW, uri), PackageManager.MATCH_ALL);
        return prepare(app, filter(infos, app.getPackageName(), new HiddenAppsStore(app).list()));
    }

    static List<Item> prepare(Context context, List<ResolveInfo> infos) {
        Context app = context.getApplicationContext();
        observe(app);
        PackageManager pm = app.getPackageManager();
        String configuration = ":" + app.getResources().getConfiguration().hashCode();
        List<Item> result = new ArrayList<>();
        for (ResolveInfo info : infos) {
            if (Thread.currentThread().isInterrupted()) break;
            String key = info.activityInfo.packageName + "/" + info.activityInfo.name + configuration;
            Metadata metadata = CACHE.get(key);
            Drawable icon;
            if (metadata == null) {
                CharSequence label = info.loadLabel(pm);
                icon = info.loadIcon(pm);
                metadata = new Metadata(label, icon == null ? null : icon.getConstantState());
                CACHE.put(key, metadata);
            } else icon = metadata.icon == null ? null : metadata.icon.newDrawable(app.getResources());
            result.add(new Item(info, metadata.label, icon == null ? null : icon.mutate()));
        }
        return result;
    }
}

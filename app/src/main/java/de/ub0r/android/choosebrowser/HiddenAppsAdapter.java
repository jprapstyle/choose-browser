package de.ub0r.android.choosebrowser;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

public class HiddenAppsAdapter extends RecyclerView.Adapter<HiddenAppsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        final View containerView;
        final TextView activityNameView;
        final ImageView iconView;
        final CheckBox hiddenView;

        ViewHolder(final View itemView) {
            super(itemView);
            activityNameView = itemView.findViewById(R.id.activity_name);
            iconView = itemView.findViewById(R.id.activity_icon);
            hiddenView = itemView.findViewById(R.id.hidden);
            containerView = itemView;
        }
    }

    private final LayoutInflater mInflater;
    private final PackageManager mPackageManager;
    private final HiddenAppsStore mStore;
    private final Set<String> mHidden;
    private final List<BrowserCatalog.Item> mItems;

    HiddenAppsAdapter(final Context context, final HiddenAppsStore store, final List<BrowserCatalog.Item> browsers) {
        mInflater = LayoutInflater.from(context);
        mPackageManager = context.getPackageManager();
        mStore = store;
        mHidden = store.list();
        mItems = browsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_hidden_app, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BrowserCatalog.Item browser = mItems.get(position);
        final String packageName = browser.info.activityInfo.packageName;
        holder.activityNameView.setText(browser.label);
        holder.iconView.setImageDrawable(browser.icon);
        holder.hiddenView.setChecked(mHidden.contains(packageName));
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final boolean hidden = !holder.hiddenView.isChecked();
                holder.hiddenView.setChecked(hidden);
                if (hidden) {
                    mHidden.add(packageName);
                } else {
                    mHidden.remove(packageName);
                }
                mStore.setHidden(packageName, hidden);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

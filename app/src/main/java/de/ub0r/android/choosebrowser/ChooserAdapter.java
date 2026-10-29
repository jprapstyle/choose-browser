package de.ub0r.android.choosebrowser;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChooserAdapter extends RecyclerView.Adapter<ChooserAdapter.ViewHolder> {

    interface OnItemClickListener {
        void onItemClick(final ComponentName component);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final View containerView;
        final TextView activityNameView;
        final ImageView iconView;

        ViewHolder(final View itemView) {
            super(itemView);
            activityNameView = itemView.findViewById(R.id.activity_name);
            iconView = itemView.findViewById(R.id.activity_icon);
            containerView = itemView;
        }

    }

    private final LayoutInflater mInflater;
    private final OnItemClickListener mListener;
    private final List<BrowserCatalog.Item> mItems;

    ChooserAdapter(final Context context, final OnItemClickListener listener, final List<BrowserCatalog.Item> browsers) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mItems = browsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View itemView = mInflater.inflate(R.layout.item_chooser,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BrowserCatalog.Item content = mItems.get(position);
        holder.activityNameView.setText(content.label);
        holder.iconView.setImageDrawable(content.icon);
        if (mListener != null) {
            holder.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mListener.onItemClick(content.component());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

package com.cannic.apps.rlbubble.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cannic.apps.rlbubble.R;
import com.cannic.apps.rlbubble.io.DatabaseHelper;
import com.cannic.apps.rlbubble.java.App;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    //private static final int TYPE_PERMISSION = 1;
    private static final int TYPE_HEADER = 2;

    private Context context;
    private LayoutInflater layoutInflater;
    private Listener listener;
    private List<App> appList;
    private List<CheckBox> checkBoxes;

    public AppAdapter(Context context, Listener listener, List<App> customizedListView, List<CheckBox> checkBoxes) {
        this.context = context;
        this.listener = listener;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appList = customizedListView;
        this.checkBoxes = checkBoxes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            /*case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_excluded_apps_header, parent, false);
                return new PermissionViewHolder(view);*/
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_excluded_apps_header, parent, false);
                return new HeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_installed_app, parent, false);
                return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            checkBoxes.add(itemViewHolder.cbAppSelected);

            itemViewHolder.tv_app_name.setText(appList.get(position).getName());
            itemViewHolder.iv_app_icon.setImageDrawable(appList.get(position).getIcon());
            itemViewHolder.cbAppSelected.setChecked(appList.get(position).isChecked());

            itemViewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appList.get(position).isChecked())
                        DatabaseHelper.ExceptionHelper.deleteException(appList.get(position));
                    else
                        DatabaseHelper.ExceptionHelper.insertException(appList.get(position));

                    appList.get(position).setChecked(!appList.get(position).isChecked());

                    itemViewHolder.cbAppSelected.setChecked(!itemViewHolder.cbAppSelected.isChecked());

                    listener.dataSetChanged();
                }
            });
            itemViewHolder.cbAppSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appList.get(position).isChecked())
                        DatabaseHelper.ExceptionHelper.deleteException(appList.get(position));
                    else
                        DatabaseHelper.ExceptionHelper.insertException(appList.get(position));

                    appList.get(position).setChecked(!appList.get(position).isChecked());

                    listener.dataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        /*if (position == 0)
            return TYPE_PERMISSION;*/
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class PermissionViewHolder extends RecyclerView.ViewHolder {

        PermissionViewHolder(View view) {
            super(view);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View view) {
            super(view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item;
        ImageView iv_app_icon;
        TextView tv_app_name;
        CheckBox cbAppSelected;

        ItemViewHolder(View view) {
            super(view);

            item = view.findViewById(R.id.item);
            iv_app_icon = view.findViewById(R.id.iv_app_icon);
            tv_app_name = view.findViewById(R.id.tv_app_name);
            cbAppSelected = view.findViewById(R.id.cb_app_selected);
        }
    }

    public interface Listener {
        void dataSetChanged();
    }
}
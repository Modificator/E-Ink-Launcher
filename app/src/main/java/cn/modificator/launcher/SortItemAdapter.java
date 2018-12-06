package cn.modificator.launcher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.modificator.launcher.filemanager.FileAdapter;

public class SortItemAdapter extends BaseAdapter {
    PackageManager packageManager;
    private Context mContext;
    private List<ComparableResolveInfo> appList = new ArrayList<>();
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount () {
        return appList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem (int position) {
        return appList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId (int position) {
        return position;
    }


    public SortItemAdapter (Context context, List<ComparableResolveInfo> mApps, PackageManager pm){
        mContext = context;
        appList = mApps;
        packageManager = pm;
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sort_item, null);
            viewHolder.sortItemIcon = convertView.findViewById(R.id.sort_item_icon);
            viewHolder.sortItemName = convertView.findViewById(R.id.sort_item_name);
            viewHolder.sortUpBtn = convertView.findViewById(R.id.sort_up_btn);
            viewHolder.sortDownBtn = convertView.findViewById(R.id.sort_down_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.sortItemIcon.setImageDrawable(appList.get(position).getResolveInfo().loadIcon(packageManager));
        viewHolder.sortItemName.setText(appList.get(position).getResolveInfo().loadLabel(packageManager));
        viewHolder.sortUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                onSortItemUpListener.onUpClick(position);
            }
        });
        viewHolder.sortDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                onSortItemDownListener.onDownClick(position);
            }
        });

        return convertView;
    }


    public interface OnSortItemUpListener{
        void onUpClick(int i);
    }
    public interface OnSortItemDownListener{
        void onDownClick(int i);
    }
    private OnSortItemUpListener onSortItemUpListener;
    private OnSortItemDownListener onSortItemDownListener;
    public void SetSortItemBtnClickListener(OnSortItemUpListener u,OnSortItemDownListener d){
        onSortItemUpListener = u;
        onSortItemDownListener = d;
    }
    class ViewHolder {
        ImageView sortItemIcon;
        TextView sortItemName;
        Button sortUpBtn;
        Button sortDownBtn;
    }
}

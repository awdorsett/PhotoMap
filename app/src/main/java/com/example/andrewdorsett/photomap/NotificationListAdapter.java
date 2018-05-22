package com.example.andrewdorsett.photomap;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew Dorsett on 2/18/18.
 */

public class NotificationListAdapter extends BaseAdapter {
    private List<MarkerGroup> groups;
    private Context context;
    private Context itemContext;
    private HashMap<String, Bitmap> thumbnailMap = new HashMap<>();

    public NotificationListAdapter(Context context, List<MarkerGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int i) {
        if (i < groups.size()) {
            return groups.get(i);
        }

        return null;
    }

    @Override
    public long getItemId(int i) {
        return -1;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        GroupViewHolder viewHolder;

        final View result;

        if (view == null) {

            viewHolder = new GroupViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            itemContext = inflater.getContext();
            view = inflater.inflate(R.layout.layout_group_list_item, parent, false);
            viewHolder.groupTitle = view.findViewById(R.id.group_title);
            viewHolder.groupInfo = view.findViewById(R.id.group_info);
            viewHolder.groupImage = view.findViewById(R.id.group_image);

            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
            result = view;
        }

        MarkerGroup group = groups.get(pos);
        viewHolder.groupTitle.setText(group.getTitle());
        viewHolder.groupInfo.setText("Image Count: " + group.getMarkers().size());
        if (!group.getMarkers().isEmpty()) {
            viewHolder.groupImage.setImageBitmap(getThumbnail(group));
        }

        return view;
    }

    private static class GroupViewHolder {

        TextView groupTitle;
        TextView groupInfo;
        ImageView groupImage;
    }

    private Bitmap getThumbnail(MarkerGroup group) {

        if (!thumbnailMap.containsKey(group.getTitle())) {
            Uri uri = group.getMarkers().get(0).getImageUri();
            int height = (int) context.getResources().getDimension(R.dimen.group_list_max_height);
            thumbnailMap.put(group.getTitle(),ImageResizeUtil.getScaledBitMap(itemContext.getContentResolver(), uri, height, height));
        }

        return thumbnailMap.get(group.getTitle());
    }
}

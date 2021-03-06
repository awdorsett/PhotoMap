package com.example.andrewdorsett.photomap;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew Dorsett on 2/18/18.
 */

public class ImageListAdapter extends BaseAdapter {
    protected List<ImageMarker> images;
    private Context context;
    protected Context itemContext;
    private HashMap<Uri, Bitmap> thumbnailMap = new HashMap<>();
    private boolean isCheckboxVisible = false;

    public ImageListAdapter(Context context, List<ImageMarker> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        if (i < images.size()) {
            return images.get(i);
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
            viewHolder.selected = view.findViewById(R.id.select_checkbox);
            viewHolder.selected.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
            viewHolder.selected.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
            result = view;
        }

        ImageMarker image = images.get(pos);
        viewHolder.groupTitle.setText(image.getTitle());
        if (image.getImageUri() != null) {
            viewHolder.groupImage.setImageBitmap(getThumbnail(image.getImageUri()));
        }

        return view;
    }

    public void setCheckboxVisible(boolean value) {
        if (isCheckboxVisible != value) {
            isCheckboxVisible = value;
            notifyDataSetChanged();
        }
    }


    private static class GroupViewHolder {

        TextView groupTitle;
        TextView groupInfo;
        ImageView groupImage;
        CheckBox selected;
    }

    protected Bitmap getThumbnail(Uri imageUri) {

        if (!thumbnailMap.containsKey(imageUri)) {;
            int height = (int) context.getResources().getDimension(R.dimen.group_list_max_height);
            thumbnailMap.put(imageUri,ImageResizeUtil.getScaledBitMap(itemContext.getContentResolver(), imageUri, height, height));
        }

        return thumbnailMap.get(imageUri);
    }
}

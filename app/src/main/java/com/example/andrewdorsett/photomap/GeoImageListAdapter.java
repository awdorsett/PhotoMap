package com.example.andrewdorsett.photomap;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.HashMap;
import java.util.List;

public class GeoImageListAdapter extends ImageListAdapter {
    private HashMap<Uri, Place> placesMap;
    private Context context;

    public GeoImageListAdapter(Context context, List<ImageMarker> images, HashMap<Uri, Place> placesMap) {
        super(context, images);
        this.placesMap = placesMap;
        this.context = context;
    }

    // TODO DRY out code and make inheritance proper
    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        GeoGroupViewHolder viewHolder;

        final View result;

        if (view == null) {

            viewHolder = new GeoGroupViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            itemContext = inflater.getContext();
            view = inflater.inflate(R.layout.layout_geo_image_list_item, parent, false);
            viewHolder.image = view.findViewById(R.id.image);
            viewHolder.locality = view.findViewById(R.id.location_title);
            viewHolder.statusImage = view.findViewById(R.id.location_status_image);

            view.setTag(viewHolder);
        } else {
            viewHolder = (GeoGroupViewHolder) view.getTag();
        }

        ImageMarker image = images.get(pos);

        if (image.getImageUri() != null) {
            viewHolder.image.setImageBitmap(getThumbnail(image.getImageUri()));
        }

        if (placesMap.containsKey(image.getImageUri())) {
            viewHolder.locality.setText(placesMap.get(image.getImageUri()).getAddress());
        }

        return view;
    }

    // TODO move this out along with ImageListAdapter
    protected static class GeoGroupViewHolder{
        protected ImageView image;
        protected TextView locality;
        protected ImageView statusImage;
    }
}

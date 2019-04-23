package com.example.andrewdorsett.photomap;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew dorsett on 12/31/17.
 */

public class MarkerOptions {
    private MarkerGroup group = null;
    private LatLng latLng;
    private MarkerOptions options = null;

    public MarkerOptions(MarkerGroup group, double latitude, double longitude) {
        this.group = group;
        this.latLng = new LatLng(latitude, longitude);
        options = OptionsManager.generateOptions(group);
    }

    public void setLatLng(double latitude, double longitude) {
        this.latLng = new LatLng(latitude, longitude);
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    publicc MarkerGroup getGroup(){
        return this.group;
    }

    public MarkerOptions getOptions() {
        return this.options;
    }


}
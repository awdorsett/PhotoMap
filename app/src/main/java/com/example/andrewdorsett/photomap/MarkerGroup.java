package com.example.andrewdorsett.photomap;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 12/31/17.
 */

public class MarkerGroup implements Parcelable {
    private List<ImageMarker> markers = new ArrayList<>();
    private long id = -1;
    private String key;
    private LatLng latLng;

    public MarkerGroup(String key, double latitude, double longitude) {
        this.key = key;
        this.latLng = new LatLng(latitude, longitude);
    }

    public MarkerGroup(Parcel in) {
        id = in.readLong();
        in.readList(markers, ImageMarker.CREATOR.getClass().getClassLoader());
        key = in.readString();
        latLng = new LatLng(in.readDouble(), in.readDouble());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MarkerGroup createFromParcel(Parcel in) {
            return new MarkerGroup(in);
        }

        public MarkerGroup[] newArray(int size) {
            return new MarkerGroup[size];
        }
    };


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ImageMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<ImageMarker> markers) {
        this.markers = markers;
    }

    public void setMarker(ImageMarker marker) {
        markers.add(marker);
    }

    public void clearMarkers() {
        markers = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(double latitude, double longitude) {
        latLng = new LatLng(latitude, longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeList(markers);
        parcel.writeString(key);
        parcel.writeDouble(latLng.latitude);
        parcel.writeDouble(latLng.longitude);
    }
}

package com.example.andrewdorsett.photomap;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

/**
 * Created by andrew dorsett on 12/26/17.
 */

public class ImageMarker implements Parcelable, ClusterItem {
    public static final Integer DEFAULT_GROUP_ID = 1;
    private long id = -1;
    private String title;
    private LatLng latLng;
    private Date originalDate;
    private Date addedDate;
    private int groupId;
    private Uri imageUri;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ImageMarker createFromParcel(Parcel in) {
            return new ImageMarker(in);
        }

        public ImageMarker[] newArray(int size) {
            return new ImageMarker[size];
        }
    };

    public ImageMarker() {
        this(-1, null, 0, 0, null, null, null, DEFAULT_GROUP_ID);
    }

    public ImageMarker(long id, String title, double latitude, double longitude, Date originalDate, Date addedDate, Uri imageUri, int groupId) {
        this.id = id;
        this.title = title;
        latLng = new LatLng(latitude, longitude);
        this.originalDate = originalDate;
        this.addedDate = addedDate;
        this.imageUri = imageUri;
        this.groupId = groupId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return getTitle();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public void setLatLng(double latitude, double longitude) {
        this.latLng = new LatLng(latitude, longitude);
    }

    public Date getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(Date originalDate) {
        this.originalDate = originalDate;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeDouble(latLng.latitude);
        parcel.writeDouble(latLng.longitude);
        parcel.writeLong(originalDate.getTime());
        parcel.writeLong(addedDate.getTime());
        parcel.writeString(imageUri.toString());
        parcel.writeInt(groupId);
    }

    public ImageMarker(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        latLng = new LatLng(in.readDouble(), in.readDouble());
        this.originalDate = new Date(in.readLong());
        this.addedDate = new Date(in.readLong());
        this.imageUri = Uri.parse(in.readString());
        this.groupId = in.readInt();
    }

    @Override
    public int hashCode() {
        int hash = 37;
        hash += id;
        hash += imageUri.hashCode();
        hash += latLng.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object second) {
        if (second instanceof ImageMarker) {
            return this.id == ((ImageMarker) second).id
                    && this.imageUri.equals(((ImageMarker) second).getImageUri())
                    && this.latLng.equals(((ImageMarker) second).latLng);
        }
        return false;
    }
}

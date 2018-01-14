package com.example.andrewdorsett.photomap;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifImageDirectory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static com.drew.metadata.exif.ExifDirectoryBase.TAG_DATETIME_ORIGINAL;

public class MainActivity extends AppCompatActivity {
    private static int PICK_IMAGE = 101;
    private static int OPEN_IMAGE_SELECT = 102;
//    private ArrayList<ImageMarker> imageMarkers = new ArrayList<>();
    private ArrayList<MarkerGroup> groups = new ArrayList<>();
    private HashMap<String, MarkerGroup> groupMap = new HashMap<>();
    private Date latestDate = null;
    private Geocoder geocoder;
    private MarkerSQLiteOpenHelper sqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlHelper = new MarkerSQLiteOpenHelper(this);
        geocoder = new Geocoder(this);

        Button imageButton = (Button) findViewById(R.id.imageButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        groups = sqlHelper.getGroups();

        if (groups.size() > 0) {
            launchMaps(null);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery(view);
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMaps(view);
            }
        });

        // Drop tables for testing only
        // TODO Remove after testing is done
        // sqlHelper.resetTables();
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groups = new ArrayList<>();
                sqlHelper.resetTables();
            }
        });
    }

    public void launchMaps(View view) {
        // TODO move the save to DB
        sqlHelper.saveGroupToDB(groups);
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setAction(ACTION_OPEN_DOCUMENT);
        // TODO update key with static enum
        if (groups.size() > 0) {
            intent.putParcelableArrayListExtra("groups", groups);
        }

        startActivity(intent);
    }

    public void launchGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<ImageMarker> imageMarkers = new ArrayList<>();
        MarkerSQLiteOpenHelper db = new MarkerSQLiteOpenHelper(this);

        if (requestCode == PICK_IMAGE && data != null) {
            if (data.getData() != null) {
                ImageMarker marker = getMarker(data.getData(), data);
                if (marker != null) {
                    imageMarkers.add(marker);
                    addImageMarkerToGroup(marker);
                }
            } else if (data.getClipData() != null ) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ImageMarker marker = getMarker(clipData.getItemAt(i).getUri(), data);
                    if (marker != null) {
                        imageMarkers.add(marker);
                        addImageMarkerToGroup(marker);
                    }
                }
            }
        } else if (requestCode == OPEN_IMAGE_SELECT) {
            launchGallery(null);
        }
    }

    private ImageMarker getMarker(Uri imageUri, Intent intent) {
        ImageMarker marker = new ImageMarker();
        marker.setImageUri(imageUri);

        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(imageUri, takeFlags);

            try {
                InputStream in = getContentResolver().openInputStream(imageUri);

                Metadata metadata = ImageMetadataReader.readMetadata(in);

                // Set location
                if (metadata.getDirectoriesOfType(GpsDirectory.class) != null) {
                    GpsDirectory directory =
                            metadata.getDirectoriesOfType(GpsDirectory.class).iterator().next();

                    marker.setLatLng(directory.getGeoLocation().getLatitude(),
                            directory.getGeoLocation().getLongitude());


                    // Set Exif data
                    if (metadata.containsDirectoryOfType(ExifSubIFDDirectory.class)) {
                        ExifSubIFDDirectory imageDirectory = metadata
                                .getDirectoriesOfType(ExifSubIFDDirectory.class).iterator().next();

                        marker.setTitle(imageDirectory.getName());

                        Date imageDate = imageDirectory.getDateOriginal();
                        marker.setOriginalDate(imageDate);
                        marker.setAddedDate(new Date());

                        if (latestDate == null) {
                            latestDate = imageDate;
                        } else if (imageDate.getTime() > latestDate.getTime()) {
                            latestDate = imageDate;
                        }
                    }
                }
            } catch (Exception e) {

            }

        return marker;
    }

    private void addImageMarkerToGroup(ImageMarker marker) {
        // TODO catch appropriate exception
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    marker.getLatLng().latitude,
                    marker.getLatLng().longitude,
                    1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                MarkerGroup group;

                if (groupMap.containsKey(address.getLocality())) {
                    group = groupMap.get(address.getLocality());
                } else {
                    group = new MarkerGroup(address.getLocality(), address.getLatitude(), address.getLongitude());
                    groupMap.put(address.getLocality(), group);
                    groups.add(group);
                }

                group.setMarker(marker);
            }
        } catch (Exception e) {}
    }

    private void getGroupsFromDB() {
        sqlHelper.getGroups();
    }
}

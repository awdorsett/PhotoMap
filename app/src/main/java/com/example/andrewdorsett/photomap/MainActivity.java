package com.example.andrewdorsett.photomap;

import android.content.ClipData;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static com.example.andrewdorsett.photomap.Constants.COMPLETE_IMAGE_KEY;
import static com.example.andrewdorsett.photomap.Constants.GROUPS_KEY;
import static com.example.andrewdorsett.photomap.Constants.INCOMPLETE_IMAGES_KEY;
import static com.example.andrewdorsett.photomap.Constants.OPEN_IMAGE_SELECT;
import static com.example.andrewdorsett.photomap.Constants.PICK_IMAGE;
import static com.example.andrewdorsett.photomap.Constants.SELECT_GEO;
import static com.example.andrewdorsett.photomap.Constants.SELECT_GROUP_KEY;
import static com.example.andrewdorsett.photomap.Constants.UPDATED_GROUP_KEY;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private ArrayList<MarkerGroup> groups = new ArrayList<>();
    private HashMap<String, MarkerGroup> groupMap = new HashMap<>();
    private Date latestDate = null;
    private Geocoder geocoder;
    private MarkerSQLiteOpenHelper sqlHelper;
    private ArrayList<ImageMarker> incompleteMarkers = new ArrayList<>();
//    private FloatingActionButton incompleteImageButton;
    ListView groupList;
    RelativeLayout notificationBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlHelper = new MarkerSQLiteOpenHelper(this);
        geocoder = new Geocoder(this);

        Button imageButton = findViewById(R.id.imageButton);
        Button mapButton = findViewById(R.id.mapButton);
//        incompleteImageButton = findViewById(R.id.incompleteImageButton);
//        incompleteImageButton.setVisibility(View.GONE);
        //sqlHelper.resetTables(); // FOR TESTING

        groupList = findViewById(R.id.group_list);
        notificationBarLayout = findViewById(R.id.notification_bar);
        updateGroupList();

        imageButton.setOnClickListener(view -> launchGallery(view));

        mapButton.setOnClickListener(view -> launchMaps(view, null));

        notificationBarLayout.setOnClickListener(view -> {
            launchSelectGeo();
        });

        // TODO Remove after testing is done
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(view -> {
            groups = new ArrayList<>();
            sqlHelper.resetTables();
        });


    }

    public void launchMaps(View view, MarkerGroup selectedGroup) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setAction(ACTION_OPEN_DOCUMENT);

        if (groups.size() > 0) {
            intent.putParcelableArrayListExtra(GROUPS_KEY, groups);
        }

        if (selectedGroup != null) {
            intent.putExtra(SELECT_GROUP_KEY, selectedGroup);
        }

        startActivity(intent);
    }

    public void launchGallery(View view) {
        incompleteMarkers.clear();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void launchSelectGeo() {
        Bundle b = new Bundle();
        b.putParcelableArrayList(INCOMPLETE_IMAGES_KEY, incompleteMarkers);
        Intent intent = new Intent(this, SelectGeoLocation.class);
        intent.putExtra("bundle", b);
        startActivityForResult(Intent.createChooser(intent, "Select Geo Location"), SELECT_GEO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO clean up this code, use switch statement?
//        switch(resultCode){
//            case OPEN_IMAGE_SELECT:
//                break;
//            case SELECT_GEO:
//                break;
//            case PICK_IMAGE:
//                break;
//        }
        if (requestCode == PICK_IMAGE && data != null) {
            if (data.getData() != null) {
                ImageMarker marker = constructImageMarker(data.getData(), data);

                if (marker != null) {
                    addImageMarkerToGroup(marker);
                }
            } else if (data.getClipData() != null ) {
                ClipData clipData = data.getClipData();

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ImageMarker marker = constructImageMarker(clipData.getItemAt(i).getUri(), data);
                    if (marker != null) {
                        addImageMarkerToGroup(marker);
                    }
                }
            }

            sqlHelper.saveGroupToDB(groups);
            updateGroupList();
            notificationBarLayout.setVisibility(incompleteMarkers.isEmpty() ? View.GONE : View.VISIBLE);
//            incompleteImageButton.setVisibility(incompleteMarkers.isEmpty() ? View.GONE : View.VISIBLE);

            // TODO is this needed?
        } else if (requestCode == OPEN_IMAGE_SELECT) {
            launchGallery(null);
        } else if (requestCode == SELECT_GEO) {
            if (data != null && data.getBundleExtra("bundle") != null) {
                List<ImageMarker> completeImages = data.getBundleExtra("bundle").getParcelableArrayList(COMPLETE_IMAGE_KEY);
                for (ImageMarker marker : completeImages) {
                    addImageMarkerToGroup(marker);
                    incompleteMarkers.remove(marker);
                }

                sqlHelper.saveGroupToDB(groups);

                notificationBarLayout.setVisibility(incompleteMarkers.isEmpty() ? View.GONE : View.VISIBLE);
//                incompleteImageButton.setVisibility(incompleteMarkers.isEmpty() ? View.GONE : View.VISIBLE);

                updateGroupList();
            }
        }
    }


    // TODO move into service
    private ImageMarker constructImageMarker(Uri imageUri, Intent intent) {

        // Persist read permission on image
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        getContentResolver().takePersistableUriPermission(imageUri, takeFlags);

        // Get geo location and date information
        ImageMarker marker = new ImageMarker();
        marker.setImageUri(imageUri);
        addImageGeoData(marker);

        if (!marker.isLatLngSet()) {
            incompleteMarkers.add(marker);
        } else {
            return marker;
        }

        return null;
    }

    // TODO move into service
    private boolean addImageGeoData(ImageMarker marker) {
        try {
            InputStream in = getContentResolver().openInputStream(marker.getImageUri());
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
                    marker.setOriginalDate(imageDate != null ? imageDate : new Date());
                    marker.setAddedDate(new Date());

                    if (latestDate == null || imageDate.getTime() > latestDate.getTime()) {
                        latestDate = imageDate;
                    }
                } else {
                    marker.setOriginalDate(new Date());
                    marker.setAddedDate(new Date());
                }
            }
        } catch (Exception e) {
                Log.e(TAG, "Failed getting location data for: " + marker.getImageUri());
        }

        return marker.isLatLngSet();
    }

    private void addImageMarkerToGroup(ImageMarker marker) {
        // TODO catch appropriate exception
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    marker.getPosition().latitude,
                    marker.getPosition().longitude,
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

                group.addMarker(marker);
            }
        } catch (Exception e) {

        }
    }

    private void updateGroupList(){
        groupMap.clear();
        groups = sqlHelper.getGroups();
        for (MarkerGroup group : groups) {
            if (groupMap.containsKey(group.getKey())) {
                // TODO - This should throw an error since you should not have 2 localities
            } else {
                groupMap.put(group.getKey(), group);
            }
        }
        GroupListAdapter groupListAdapter = new GroupListAdapter(groupList.getContext(), groups);
        groupList.setAdapter(groupListAdapter);
        groupList.setOnItemClickListener((adapterView, view, pos, id) -> {
            MarkerGroup group = groups.get(pos);
            launchMaps(adapterView, group);
        });
    }
}

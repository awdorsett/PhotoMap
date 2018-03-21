package com.example.andrewdorsett.photomap;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static com.example.andrewdorsett.photomap.Constants.GROUPS_KEY;
import static com.example.andrewdorsett.photomap.Constants.OPEN_IMAGE_SELECT;
import static com.example.andrewdorsett.photomap.Constants.SELECT_GROUP_KEY;
import static com.example.andrewdorsett.photomap.Constants.TITLE_KEY;
import static com.example.andrewdorsett.photomap.Constants.URI_KEY;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        ClusterManager.OnClusterItemClickListener<MarkerGroup>{

    private GoogleMap mMap;
    private List<MarkerGroup> groups = new ArrayList<>();
    private Map<Marker, MarkerGroup> markerMap = new HashMap<>();
    private ClusterManager<MarkerGroup> clusterManager;
    private int zoomPositionForDisplay = 8;
    private MarkerGroup selectedGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent.hasExtra(GROUPS_KEY)) {
            groups = intent.getParcelableArrayListExtra(GROUPS_KEY);
        }
        if (intent.hasExtra(SELECT_GROUP_KEY)) {
            selectedGroup = intent.getParcelableExtra(SELECT_GROUP_KEY);
        }

        Button addImage = findViewById(R.id.addImageButton);
        addImage.setOnClickListener(view -> {
            setResult(OPEN_IMAGE_SELECT);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (MarkerGroup group : groups) {
            LatLng groupLoc = group.getPosition();
            Marker mapMarker = mMap.addMarker(new MarkerOptions()
                    .position(groupLoc)
                    .title(group.getKey()));
            mapMarker.setVisible(false);
            markerMap.put(mapMarker, group);
            if (selectedGroup == null || group.equals(selectedGroup)) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(groupLoc));
            }
        }

        setUpClusterer();
    }

    //implement the onClusterItemClick interface
    @Override
    public boolean onClusterItemClick(MarkerGroup clusterItem){
        ArrayList<Uri> imageUris = new ArrayList<>();

        for (ImageMarker imageMarker : clusterItem.getMarkers()) {
            imageUris.add(imageMarker.getImageUri());
        }

        Intent intent = new Intent(this, ImageGalleryDisplayActivity.class);
        intent.putExtra(TITLE_KEY, clusterItem.getKey());
        intent.putParcelableArrayListExtra(URI_KEY, imageUris);
        intent.setAction(ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

        return false;
    }

    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MarkerGroup>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        clusterManager.addItems(markerMap.values());
        clusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, clusterManager));
        clusterManager.setOnClusterItemClickListener(this);

        clusterManager
                .setOnClusterClickListener(cluster -> {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            cluster.getPosition(), (float) Math.floor(mMap
                                    .getCameraPosition().zoom + 1)), 300,
                            null);
                    return true;
                });

    }
}

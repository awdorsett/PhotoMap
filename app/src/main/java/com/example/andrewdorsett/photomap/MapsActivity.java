package com.example.andrewdorsett.photomap;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraMoveStartedListener {

    private GoogleMap mMap;
    private List<MarkerGroup> groups = new ArrayList<>();
    private Map<Marker, MarkerGroup> markerMap = new HashMap<>();
    private ClusterManager<MarkerGroup> clusterManager;
    private int zoomPositionForDisplay = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent.hasExtra("groups")) {
            groups = intent.getParcelableArrayListExtra("groups");
        }

        Button addImage = findViewById(R.id.addImageButton);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(102);
                finish();
            }
        });
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == REASON_GESTURE) {
            for(Marker marker : markerMap.keySet()){
//                marker.setVisible(mMap.getCameraPosition().zoom > zoomPositionForDisplay);
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (MarkerGroup group : groups) {
            LatLng groupLoc = group.getPosition();
            Marker mapMarker = mMap.addMarker(new MarkerOptions()
                    .position(groupLoc)
                    .title(group.getKey()));
            mapMarker.setVisible(mMap.getCameraPosition().zoom > zoomPositionForDisplay);
            markerMap.put(mapMarker, group);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(groupLoc));
        }

        mMap.setOnMarkerClickListener(this);
        setUpClusterer();

        mMap.setOnCameraMoveStartedListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (markerMap.containsKey(marker)) {
            ArrayList<Uri> imageUris = new ArrayList<>();
            for (ImageMarker imageMarker : markerMap.get(marker).getMarkers()) {
                imageUris.add(imageMarker.getImageUri());
            }
            Intent intent = new Intent(this, ImageGalleryDisplayActivity.class);
            // TODO update key to static
            intent.putExtra("group_title", markerMap.get(marker).getKey());
            intent.putParcelableArrayListExtra("uris", imageUris);
            intent.setAction(ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }

        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void launchImageSelect() {
        finishActivity(102);
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

    }
}

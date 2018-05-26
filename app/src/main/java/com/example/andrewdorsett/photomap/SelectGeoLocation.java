package com.example.andrewdorsett.photomap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.andrewdorsett.photomap.Constants.COMPLETE_IMAGE_KEY;
import static com.example.andrewdorsett.photomap.Constants.INCOMPLETE_IMAGES_KEY;
import static com.example.andrewdorsett.photomap.Constants.SELECT_GEO;

public class SelectGeoLocation extends AppCompatActivity {
    private List<ImageMarker> incompleteImages;
    private ArrayList<ImageMarker> completeImages = new ArrayList<>();
    private ImageMarker selectedImage = null;
    private MarkerGroup selectedGroup = null;
    private RelativeLayout groupSelectLayout = null;
    private MarkerSQLiteOpenHelper sqlHelper = new MarkerSQLiteOpenHelper(this);
    private Spinner groupSelectSpinner = null;
    private     ListView imageList;
    // TODO put this into its own adapter
    private Map<String, MarkerGroup> groupMap = new HashMap<>();

    /**
     * Display list of images
     * On click launch map for location
     * Confirm location
     * Set location to marker and remove from group
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_geo_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton saveButton = findViewById(R.id.saveLocations);
        saveButton.setVisibility(completeImages.isEmpty() ? View.GONE : View.VISIBLE);
        groupSelectLayout = findViewById(R.id.group_select);
        groupSelectLayout.setVisibility(View.GONE);
        groupSelectSpinner = findViewById(R.id.group_spinner);
        Intent intent = getIntent();
        if (intent.hasExtra("bundle")) {
            Bundle bundle = intent.getBundleExtra("bundle");
            List<MarkerGroup> groups = sqlHelper.getGroups();
            List<String> groupTitles = groups.stream().map(MarkerGroup::getTitle).collect(Collectors.toList());
            groups.forEach((group) -> {
                groupMap.put(group.getTitle(), group);
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupTitles);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSelectSpinner.setAdapter(adapter);
            Button groupSelectCancel = findViewById(R.id.cancel_selection);

            incompleteImages = bundle.getParcelableArrayList(INCOMPLETE_IMAGES_KEY);
            imageList = findViewById(R.id.image_list);

            ImageListAdapter imageListAdapter = new ImageListAdapter(imageList.getContext(), incompleteImages);
            imageList.setAdapter(imageListAdapter);
            imageList.setOnItemClickListener((adapterView, view, pos, id) -> {
                selectedImage = (ImageMarker) imageList.getItemAtPosition(pos);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), 1);
                } catch (Exception e) {
                    Log.e("SelectGeoLoc", "onCreate: " + e.getMessage() );
                }
            });

            imageList.setOnItemLongClickListener((adapterView, view, pos, id) -> {
                imageListAdapter.setCheckboxVisible(true);
                imageListAdapter.notifyDataSetChanged();
                groupSelectLayout.setVisibility(View.VISIBLE);
                String selection = (String) groupSelectSpinner.getSelectedItem();
                selectedGroup = groupMap.get(selection);
                return true;
            });
            groupSelectCancel.setOnClickListener((view) -> {
                selectedGroup = null;
                imageListAdapter.setCheckboxVisible(false);
            });
        } else {
            // TODO error message
        }

        saveButton.setOnClickListener((view) -> {
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(COMPLETE_IMAGE_KEY, completeImages);
            data.putExtra("bundle", bundle);
            setResult(SELECT_GEO, data);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Place place = PlacePicker.getPlace(this, data);
            placesMap.put(selectedImage.getImageUri(), place);
            selectedImage.setLatLng(place.getLatLng());
            completeImages.add(selectedImage);
            updateImageList();
        } else {
            selectedImage = null;
        }
    }

    private void updateImageList() {
        saveButton.setVisibility(completeImages.size() > 0 ? View.VISIBLE : View.INVISIBLE);
        ImageListAdapter imageListAdapter = new GeoImageListAdapter(imageList.getContext(), incompleteImages, placesMap);
        imageList.setAdapter(imageListAdapter);
        imageList.setOnItemClickListener((adapterView, view, pos, id) -> {
            selectedImage = (ImageMarker) imageList.getItemAtPosition(pos);
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), 1);
            } catch (Exception e) {
                Log.e("SelectGeoLoc", "onCreate: " + e.getMessage() );
            }
        });
    }
}

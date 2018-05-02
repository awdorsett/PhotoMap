package com.example.andrewdorsett.photomap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.example.andrewdorsett.photomap.Constants.INCOMPLETE_IMAGES_KEY;

public class SelectGeoLocation extends AppCompatActivity {
    private List<ImageMarker> incompleteImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_geo_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent.hasExtra(INCOMPLETE_IMAGES_KEY)) {
            incompleteImages = intent.getParcelableArrayListExtra(INCOMPLETE_IMAGES_KEY);
        } else {
            // TODO error message
        }
    }


    /**
     * Display list of images
     * On click launch map for location
     * Confirm location
     * Set location to marker and remove from group
     */

}

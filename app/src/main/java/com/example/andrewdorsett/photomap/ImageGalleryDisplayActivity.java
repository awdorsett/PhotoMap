package com.example.andrewdorsett.photomap;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;

public class ImageGalleryDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CollapsingToolbarLayout layout = findViewById(R.id.toolbar_layout);

        // TODO: pass group title once groups are set up.
        // TODO: set parameters as static references
        if (getIntent().hasExtra("group_title")) {
            // Nothing currently
        } else {
            layout.setTitle("Photos");
        }

        if (getIntent().hasExtra("uri")) {
            Uri uri = getIntent().getParcelableExtra("uri");

            ImageView image = findViewById(R.id.displayImage);
            // Test duplicate images
            // TODO: Remove
            ImageView image2 = findViewById(R.id.displayImage2);
            ImageView image3 = findViewById(R.id.displayImage3);

            image.setImageURI(uri);
            image2.setImageURI(uri);
            image3.setImageURI(uri);
        }
    }
}

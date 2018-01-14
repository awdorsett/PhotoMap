package com.example.andrewdorsett.photomap;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

public class ImageGalleryDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout container = findViewById(R.id.displayImageContainer);

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
            layout.setTitle(getIntent().getStringExtra("group_title"));
        } else {
            layout.setTitle("Photos");
        }


        if (getIntent().hasExtra("uris")) {
            List<Uri> uris = getIntent().getParcelableArrayListExtra("uris");

            for (Uri uri : uris) {
                ImageView iv = new ImageView(getApplicationContext());

                // Set an image for ImageView
                iv.setImageURI(uri);
                iv.setPadding(0,5,0,0);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setAdjustViewBounds(true);
                // Create layout parameters for ImageView
               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // Add layout parameters to ImageView
                iv.setLayoutParams(lp);

                // Finally, add the ImageView to layout
                container.addView(iv);
            }

        }
    }
}

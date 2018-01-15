package com.example.andrewdorsett.photomap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.IOException;
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
                finish();
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
            DisplayMetrics metrics = getResources().getDisplayMetrics();

            for (Uri uri : uris) {
                ImageView iv = new ImageView(getApplicationContext());
                Bitmap bm = null;
                try {
                    bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    // TODO handle real exception
                }

                bm = Bitmap.createScaledBitmap(bm, metrics.widthPixels, getHeight(bm, metrics), false);
                // Set an image for ImageView
//                iv.setImageURI(uri);
                iv.setImageBitmap(bm);
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

    private int getHeight(Bitmap bm, DisplayMetrics dm) {
        int newHeight = bm.getHeight();
        if (bm.getWidth() > dm.widthPixels) {
            double ratio = dm.widthPixels / (double) bm.getWidth();
            newHeight = (int) Math.round(bm.getHeight() * ratio);
        }

        return newHeight;
    }
}

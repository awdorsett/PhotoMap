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

import static com.example.andrewdorsett.photomap.Constants.TITLE_KEY;
import static com.example.andrewdorsett.photomap.Constants.URI_KEY;

public class ImageGalleryDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery_display);

        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout container = findViewById(R.id.displayImageContainer);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        CollapsingToolbarLayout layout = findViewById(R.id.toolbar_layout);

        if (getIntent().hasExtra(TITLE_KEY)) {
            layout.setTitle(getIntent().getStringExtra(TITLE_KEY));
        } else {
            layout.setTitle("Photos");
        }


        if (getIntent().hasExtra(URI_KEY)) {
            List<Uri> uris = getIntent().getParcelableArrayListExtra(URI_KEY);
            DisplayMetrics metrics = getResources().getDisplayMetrics();

            for (Uri uri : uris) {
                ImageView iv = new ImageView(getApplicationContext());

                Bitmap bm = ImageResizeUtil.getScaledBitMap(this.getContentResolver(), uri, metrics);

                iv.setImageBitmap(bm);
                iv.setPadding(0,5,0,0);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setAdjustViewBounds(true);
               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.MATCH_PARENT,
                       LinearLayout.LayoutParams.WRAP_CONTENT);

                iv.setLayoutParams(lp);

                container.addView(iv);
            }

        }
    }

}

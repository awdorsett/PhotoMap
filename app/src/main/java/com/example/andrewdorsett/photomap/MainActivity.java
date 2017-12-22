package com.example.andrewdorsett.photomap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static int PICK_IMAGE = 101;
    private Uri imageUri = null;
    private LatLng imageLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imageButton = (Button) findViewById(R.id.imageButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);

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
    }

    public void launchMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);

        if (imageLat != null) {
            intent.putExtra("latLng", imageLat);
        }

        startActivity(intent);
    }

    public void launchGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                InputStream in = getContentResolver().openInputStream(imageUri);

                Metadata metadata = ImageMetadataReader.readMetadata(in);

                if (metadata.getDirectoriesOfType(GpsDirectory.class) != null) {
                    GpsDirectory directory =
                            metadata.getDirectoriesOfType(GpsDirectory.class).iterator().next();
                   imageLat = new LatLng(directory.getGeoLocation().getLatitude(), directory.getGeoLocation().getLongitude());
                }
            } catch (Exception e) {

            }

        }

    }
}

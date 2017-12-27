package com.example.andrewdorsett.photomap;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifImageDirectory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.drew.metadata.exif.ExifDirectoryBase.TAG_DATETIME_ORIGINAL;

public class MainActivity extends AppCompatActivity {
    private static int PICK_IMAGE = 101;
    private ArrayList<ImageMarker> imageMarkers = new ArrayList<>();
    private Date latestDate = null;

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

        if (imageMarkers.size() > 0) {
            intent.putParcelableArrayListExtra("markers", imageMarkers);
        }

        startActivity(intent);
    }

    public void launchGallery(View view) {
        imageMarkers = new ArrayList<>();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (data.getData() != null) {
                ImageMarker marker = getMarker(data.getData());
                if (marker != null) {
                    imageMarkers.add(getMarker(data.getData()));
                }
            } else if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ImageMarker marker = getMarker(clipData.getItemAt(i).getUri());
                    if (marker != null) {
                        imageMarkers.add(marker);
                    }
                }
            }
        }
    }

    private ImageMarker getMarker(Uri imageUri) {
        ImageMarker marker = new ImageMarker();
        marker.setImageUri(imageUri);

            try {
                InputStream in = getContentResolver().openInputStream(imageUri);

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
                        marker.setOriginalDate(imageDate);
                        marker.setAddedDate(new Date());

                        if (latestDate == null) {
                            latestDate = imageDate;
                        } else if (imageDate.getTime() > latestDate.getTime()) {
                            latestDate = imageDate;
                        }
                    }
                }
            } catch (Exception e) {

            }

        return marker;
    }
}

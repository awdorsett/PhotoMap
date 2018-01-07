package com.example.andrewdorsett.photomap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by admin on 12/26/17.
 */

public class MarkerSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String PHOTO_MAP_DB = "photoMap";

    public static final String MARKER_TABLE = "markers";
    public static final String GROUP_TABLE = "groups";

    public static final String ID = "_id";
    public static final String URI = "uri";
    public static final String ORIGINAL_DATE = "originalDate";
    public static final String ADDED_DATE = "addedDate";
    public static final String TITLE = "title";
    public static final String GROUP_ID = "groupId";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String MARKER_IDS = "markerIds";

    public static final String [] GROUP_COLUMNS = {ID, TITLE, LONGITUDE, LATITUDE, MARKER_IDS, ADDED_DATE};

//    private static final String INSERT_MARKER_STATEMENT =
//            String.format("REPLACE INTO " + MARKER_TABLE + " ("
//                    + URI + ","
//                    + TITLE + ","
//                    + ORIGINAL_DATE + ","
//                    + ADDED_DATE + ","
//                    + GROUP_ID + ") VALUES (";
//
//    private static final String INSERT_GROUP_STATEMENT =
//            String.format("REPLACE INTO " + GROUP_TABLE + " ("
//                    + TITLE + ","
//                    + LONGITUDE + ","
//                    + LATITUDE + ","
//                    + MARKER_IDS + ","
//                    + ADDED_DATE + ") VALUES (");


    public MarkerSQLiteOpenHelper(Context context) {
        super(context, PHOTO_MAP_DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + MARKER_TABLE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                URI + " TEXT, " +
                TITLE + " TEXT, " +
                LONGITUDE + " DOUBLE, " +
                LATITUDE + " DOUBLE, " +
                ORIGINAL_DATE + " LONG, " +
                ADDED_DATE + " LONG, " +
                GROUP_ID + " LONG" + ")");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + GROUP_TABLE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                LONGITUDE + " DOUBLE, " +
                LATITUDE + " DOUBLE, " +
                MARKER_IDS + " TEXT, " +
                ADDED_DATE + " LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MARKER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public List<ImageMarker> saveImageMarkersToDB(MarkerGroup group) {
        SQLiteDatabase database = this.getWritableDatabase();
        List<ImageMarker> markers = group.getMarkers();

        database.beginTransaction();
        for (ImageMarker marker : markers) {
            ContentValues values = new ContentValues();
            values.put(URI, marker.getImageUri().toString());
            values.put(TITLE, marker.getTitle());
            values.put(LONGITUDE, marker.getLatLng().longitude);
            values.put(LATITUDE, marker.getLatLng().latitude);
            values.put(ORIGINAL_DATE, marker.getOriginalDate().getTime());
            values.put(ADDED_DATE, marker.getAddedDate().getTime());
            values.put(ADDED_DATE, System.currentTimeMillis());
//            values.put(GROUP_ID, group.getId());
            long id = database.replace(MARKER_TABLE, null, values);
            if (id != -1) {
                marker.setId(id);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        return markers;
    }

    public List<MarkerGroup> saveGroupToDB(List<MarkerGroup> groups) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransaction();

        for(MarkerGroup group : groups) {
            ContentValues values = new ContentValues();
            values.put(TITLE, group.getKey());
            values.put(LONGITUDE, group.getLatLng().longitude);
            values.put(LATITUDE, group.getLatLng().latitude);
            saveImageMarkersToDB(group);
            List<Long> markerList = group.getMarkers().stream()
                    .map(ImageMarker::getId).collect(Collectors.toList());
            String markerIds = Joiner.on(",").join(markerList);
            values.put(MARKER_IDS, markerIds);
            values.put(ADDED_DATE, System.currentTimeMillis());
            Long id = database.replace(GROUP_TABLE, null, values);
            if (id != -1) {
                group.setId(id);
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();

        return groups;
    }

    public ArrayList<MarkerGroup> getGroups() {
        SQLiteDatabase database = this.getReadableDatabase();
        List<MarkerGroup> groups = new ArrayList<>();

        Cursor cursor = database.query(GROUP_TABLE, GROUP_COLUMNS, null, null,
                null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(ID));
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
            double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
            String idString = cursor.getString(cursor.getColumnIndex(MARKER_IDS));
            List<Long> idList = new ArrayList<String>(Arrays.asList(idString.split(","))).stream()
                    .map(stringId -> Long.valueOf(stringId)).collect(Collectors.toList());
            long addedDate = cursor.getLong(cursor.getColumnIndex(ADDED_DATE));
        }

        return new ArrayList<>();
    }
}

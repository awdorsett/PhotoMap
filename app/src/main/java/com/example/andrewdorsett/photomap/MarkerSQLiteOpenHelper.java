package com.example.andrewdorsett.photomap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by admin on 12/26/17.
 */

public class MarkerSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String PHOTO_MAP_DB = "photoMap3";

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
    public static final String [] MARKER_COLUMNS = {ID, URI, TITLE, LONGITUDE, LATITUDE, ORIGINAL_DATE, ADDED_DATE, GROUP_ID};

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

        sqLiteDatabase.execSQL("CREATE INDEX " + TITLE + " ON " + GROUP_TABLE + "("+ ID +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MARKER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        onCreate(sqLiteDatabase);
    }

    // Only use for testing
    public void resetTables() {
        SQLiteDatabase database =  getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + MARKER_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);

        database.execSQL("CREATE TABLE IF NOT EXISTS " + MARKER_TABLE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                URI + " TEXT, " +
                TITLE + " TEXT, " +
                LONGITUDE + " DOUBLE, " +
                LATITUDE + " DOUBLE, " +
                ORIGINAL_DATE + " LONG, " +
                ADDED_DATE + " LONG, " +
                GROUP_ID + " LONG" + ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + GROUP_TABLE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                LONGITUDE + " DOUBLE, " +
                LATITUDE + " DOUBLE, " +
                MARKER_IDS + " TEXT, " +
                ADDED_DATE + " LONG)");

        database.execSQL("CREATE INDEX " + TITLE + " ON " + GROUP_TABLE + "(" + ID + ")");
        //database.close();
    }

    public List<ImageMarker> saveImageMarkersToDB(MarkerGroup group) {
        SQLiteDatabase database = getDatabase();
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
            long id = database.replace(MARKER_TABLE, null, values);
            if (id != -1) {
                marker.setId(id);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        //database.close();

        return markers;
    }

    public List<MarkerGroup> saveGroupToDB(List<MarkerGroup> groups) {
        SQLiteDatabase database = getDatabase();
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
        //database.close();

        return groups;
    }

    public ArrayList<MarkerGroup> getGroups() {
        SQLiteDatabase database = getDatabase();
        ArrayList<MarkerGroup> groups = new ArrayList<>();

        Cursor cursor = database.query(GROUP_TABLE, GROUP_COLUMNS, null, null,
                null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
            double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
            String idString = cursor.getString(cursor.getColumnIndex(MARKER_IDS));
            List<Integer> idList = new ArrayList<String>(Arrays.asList(idString.split(","))).stream()
                    .map(Integer::valueOf).collect(Collectors.toList());
            List<ImageMarker> markers = getMarkers(idList);
            MarkerGroup group = new MarkerGroup(title, latitude, longitude);
            group.setId(id);
            group.setMarkers(markers);
            groups.add(group);
        }
        //database.close();
        cursor.close();

        return groups;
    }

    public ArrayList<ImageMarker> getMarkers(List<Integer> ids) {
        SQLiteDatabase database = getDatabase();
        ArrayList<ImageMarker> markers = new ArrayList<>();
        String [] argArray = ids.stream().map(String::valueOf).collect(Collectors.toList()).toArray(new String[ids.size()]);
        Cursor cursor = database.query(MARKER_TABLE, MARKER_COLUMNS,  ID + " IN (" + createPlaceHolders(ids.size()) + ")",
                argArray, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(URI)));
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
            double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
            long addedDate = cursor.getLong(cursor.getColumnIndex(ADDED_DATE));
            long originalDate = cursor.getLong(cursor.getColumnIndex(ADDED_DATE));
            int groupId = cursor.getInt(cursor.getColumnIndex(GROUP_ID));

            markers.add(new ImageMarker(id, title, longitude, latitude, new Date(originalDate),
                    new  Date(addedDate), uri, groupId));
        }
        //database.close();
        cursor.close();
        return markers;
    }

    private String createPlaceHolders(int size) {
        String placeholder = "";

        for (int i = 0; i < size; i++) {
            placeholder += "?";
            if (i < size - 1) {
                placeholder += ",";
            }
        }

        return placeholder;
    }

    private SQLiteDatabase getDatabase() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
        } catch (SQLException s) {
            //throw new Exception("Error with DB Open");
        }

        return db;
    }

}

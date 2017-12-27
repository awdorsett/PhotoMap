package com.example.andrewdorsett.photomap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by admin on 12/26/17.
 */

public class MarkerSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String MARKER_TABLE = "markers";
    public static final String ID = "_id";
    public static final String URI = "uri";
    public static final String ORIGINAL_DATE = "originalDate";
    public static final String ADDED_DATE = "addedDate";
    public static final String TITLE = "title";
    public static final String GROUP_ID = "groupId";

    private static final String INSERT_STATEMENT_FORMAT =
            String.format("REPLACE INTO %s (%s, %s, %s, %s, %d)", MARKER_TABLE, URI, TITLE, ORIGINAL_DATE,
                    ADDED_DATE, GROUP_ID).concat(" VALUES (%s, %s, %s, %s, %d);");


    public MarkerSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MARKER_TABLE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                URI + " TEXT, " +
                TITLE + " TEXT, " +
                ORIGINAL_DATE + " TEXT, " +
                ADDED_DATE + " TEXT, " +
                GROUP_ID + " INTEGER" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MARKER_TABLE);
        onCreate(sqLiteDatabase);
    }


    public void saveToDB(List<ImageMarker> markers) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransaction();
            for(ImageMarker marker : markers) {
                database.execSQL(String.format(INSERT_STATEMENT_FORMAT,
                        marker.getImageUri().toString(),
                        marker.getTitle(),
                        marker.getOriginalDate().toString(),
                        marker.getAddedDate().toString(),
                        marker.getGroupId()));
            }
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}

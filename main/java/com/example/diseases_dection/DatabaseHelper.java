package com.example.diseases_dection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ImageDatabase";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_IMAGES = "images";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_DATA = "image_data";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_IMAGES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE_DATA + " BLOB)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    public long insertImageData(byte[] imageData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_DATA, imageData);
        long id = db.insert(TABLE_IMAGES, null, values);
        db.close();
        if (id == -1) {
            // Log the error
            Log.e("DatabaseHelper", "Failed to insert image data into the database");
        }
        return id;
    }

    public List<byte[]> getAllImageData() {
        List<byte[]> imageDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_IMAGES, null);

        int columnIndex = cursor.getColumnIndex(COLUMN_IMAGE_DATA);

        if (cursor.moveToFirst()) {
            do {
                if (columnIndex != -1) {
                    byte[] imageData = cursor.getBlob(columnIndex);
                    imageDataList.add(imageData);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return imageDataList;
    }
}

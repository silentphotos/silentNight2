package com.afterrabble.silentnight3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.afterrabble.silentnight3.Image;
import com.afterrabble.silentnight3.db.ImageDbSchema.ImageTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rendenyoder on 11/15/17.
 */

public class ImageDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "images.db";
    private static ImageDbHelper dbHelper = null;
    private SQLiteDatabase database = null;


    public ImageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static ImageDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new ImageDbHelper(context.getApplicationContext());
        }
        return dbHelper;
    }

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    public void close() {
        close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ImageTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ImageTable.Cols.PATH + ", " +
                ImageTable.Cols.DATE + ", " +
                ImageTable.Cols.GROUP_ID +
                ")"
        );
    }

    public List<Image> getAllImages() {
        List<Image> images = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from images", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Image image = new Image();
            image.setId(cursor.getLong(0));
            image.setPath(cursor.getString(1));
            image.setDate(cursor.getString(2));
            image.setGroupId(cursor.getString(3));
            images.add(image);
            cursor.moveToNext();
        }
        cursor.close();
        return images;
    }

    public List<String> getAllImagePaths() {
        List<String> paths = new ArrayList<>();

        Cursor cursor = database.rawQuery("select path from images", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            paths.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return paths;
    }

    public List<String> getAllImageDates() {
        List<String> dates = new ArrayList<>();

        Cursor cursor = database.rawQuery("select date from images", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return dates;
    }


    public Image createImage(String path, String date, String groupId) {
        ContentValues values = new ContentValues();
        values.put("path", path);
        values.put("date", date);
        values.put("group_id", groupId);

        long insertId = database.insert("images", null, values);

        if (insertId != -1) {
            return new Image(insertId, path, date, groupId);
        }

        Log.e(TAG, "Error inserting data!");
        return null;
    }

    public void deleteImage(long id) {
        database.execSQL("delete from IMAGES where id = \"" + id + "\"");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion);
        db.execSQL("DROP TABLE IF EXISTS IMAGES");
        onCreate(db);
    }
}
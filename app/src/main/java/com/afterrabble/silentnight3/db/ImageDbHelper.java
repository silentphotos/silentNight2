package com.afterrabble.silentnight3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.afterrabble.silentnight3.db.ImageDbSchema.ImageTable;

/**
 * Created by rendenyoder on 11/15/17.
 */

public class ImageDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public ImageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ImageTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ImageTable.Cols.TITLE + ", " +
                ImageTable.Cols.DATE + ", " +
                ImageTable.Cols.GROUP_ID +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
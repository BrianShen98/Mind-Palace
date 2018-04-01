package com.example.jingyue.mindpalace.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jingyue.mindpalace.data.MindContract.MindEntry;

public class MindDbHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "MindPalace.db";
    private static final int DATABASE_VERSION = 1;

    public MindDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TABLE =

                "CREATE TABLE " + MindEntry.TABLE_NAME + " (" +
                MindEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MindEntry.COLUMN_URI        + " TEXT PRIMARY NOT NULL DEFAULT '', " + 
                MindEntry.COLUMN_TIME       + " INTEGER NOT NULL DEFAULT 0, " +
                MindEntry.COLUMN_LOCATION   + " TEXT NOT NULL DEFAULT '', " + 
                MindEntry.COLUMN_FEATURE1   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE2   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE3   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE4   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE5   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE6   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE7   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE8   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE9   + " TEXT NOT NULL DEFAULT '', " +
                MindEntry.COLUMN_FEATURE10  + " TEXT NOT NULL DEFAULT '', " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MindEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

}

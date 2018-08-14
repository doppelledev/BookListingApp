package com.example.android.booklistingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.booklistingapp.data.LibraryContract.LibraryEntry;

public class LibraryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 1;

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_LIBRARY_TABLE = "CREATE TABLE " + LibraryEntry.TABLE_NAME + "("
                + LibraryEntry._ID + " INTEGER PRIMARY KEY, "
                + LibraryEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + LibraryEntry.COLUMN_AUTHORS + " TEXT NOT NULL, "
                + LibraryEntry.COLUMN_PUBLISHER + " TEXT, "
                + LibraryEntry.COLUMN_PUBDATE + " TEXT, "
                + LibraryEntry.COLUMN_DESCRIPTION + " TEXT, "
                + LibraryEntry.COLUMN_LINK + " TEXT, "
                + LibraryEntry.COLUMN_THUMB + " BLOB);";
        sqLiteDatabase.execSQL(SQL_CREATE_LIBRARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

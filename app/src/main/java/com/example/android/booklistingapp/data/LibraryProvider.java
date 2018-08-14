package com.example.android.booklistingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android.booklistingapp.data.LibraryContract.LibraryEntry;

public class LibraryProvider extends ContentProvider {

    /**
     * Uri matcher and its matches
     * Used recognize what type of Uri is used
     */
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int WHOLE_TABLE = 0;
    private static final int SPECIFIC_ROW = 1;

    static {
        mUriMatcher.addURI(LibraryContract.CONTENT_AUTHORITY ,LibraryContract.PATH_LIBRARY, WHOLE_TABLE);
        mUriMatcher.addURI(LibraryContract.CONTENT_AUTHORITY, LibraryContract.PATH_LIBRARY + "/#", SPECIFIC_ROW);
    }


    LibraryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new LibraryDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (mUriMatcher.match(uri)) {
            case WHOLE_TABLE:
                cursor = db.query(LibraryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SPECIFIC_ROW:
                selection = LibraryEntry._ID + "=?";
                selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(LibraryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can't query, Invalid Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case WHOLE_TABLE:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Can't insert, Invalid Uri: " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String title = values.getAsString(LibraryEntry.COLUMN_TITLE);
        if (title == null || TextUtils.isEmpty(title))
            throw new IllegalArgumentException("No title provided");
        String authors = values.getAsString(LibraryEntry.COLUMN_AUTHORS);
        if (authors == null || TextUtils.isEmpty(authors))
            throw new IllegalArgumentException("No author provided");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(LibraryEntry.TABLE_NAME, null, values);
        if (id == -1)
            return null;
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}

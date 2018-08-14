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
                throw new IllegalArgumentException("Can't query. Invalid Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        switch (mUriMatcher.match(uri)) {
            case WHOLE_TABLE:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Can't insert. Invalid Uri: " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        // sanity checks
        String title = values.getAsString(LibraryEntry.COLUMN_TITLE);
        if (title == null || TextUtils.isEmpty(title))
            throw new IllegalArgumentException("No title provided");
        String authors = values.getAsString(LibraryEntry.COLUMN_AUTHORS);
        if (authors == null || TextUtils.isEmpty(authors))
            throw new IllegalArgumentException("No author provided");

        // actual insertion
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(LibraryEntry.TABLE_NAME, null, values);
        if (id == -1)
            // insertion failed
            return null;
        // insertion succeeded
        // notify changes
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (mUriMatcher.match(uri)) {
            case WHOLE_TABLE:
                rowsDeleted = db.delete(LibraryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SPECIFIC_ROW:
                selection = LibraryEntry._ID + "=?";
                selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(LibraryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Can't delete. Invalid Uri: " + uri);
        }
        if (rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (mUriMatcher.match(uri)) {
            case WHOLE_TABLE:
                return updateBooks(uri, contentValues, selection, selectionArgs);
            case SPECIFIC_ROW:
                selection = LibraryEntry.TABLE_NAME + "=?";
                selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                return updateBooks(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Can't update. Invalid Uri: " + uri);
        }
    }

    private int updateBooks(Uri uri, ContentValues values, String selection, String [] selectionArgs) {
        // sanity checks
        if (values.size() == 0)
            return 0;
        if (values.containsKey(LibraryEntry.COLUMN_TITLE)) {
            String title = values.getAsString(LibraryEntry.TABLE_NAME);
            if (title == null || TextUtils.isEmpty(title))
                throw new IllegalArgumentException("No title provided");
        }
        if (values.containsKey(LibraryEntry.COLUMN_AUTHORS)) {
            String authors = values.getAsString(LibraryEntry.COLUMN_AUTHORS);
            if (authors == null || TextUtils.isEmpty(authors))
                throw new IllegalArgumentException("No author provided");
        }

        // actual update
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(LibraryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated > 0)
            // some rows have been updated, notify
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case WHOLE_TABLE:
                return LibraryEntry.CONTENT_DIR_TYPE;
            case SPECIFIC_ROW:
                return LibraryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Can't get type. Invalid Uri: " + uri);
        }
    }
}

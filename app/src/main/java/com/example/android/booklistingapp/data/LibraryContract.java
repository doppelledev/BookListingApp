package com.example.android.booklistingapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for the library database, contains 1 table
 */
public final class LibraryContract {

    /**
     * Constants used to generate uris for the table
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.booklistingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Constant to be appended to BASE_CONTENT_URI to generate uri for the library table
    public static final String PATH_LIBRARY = "library";

    public LibraryContract() {
        // Not instantiable
    }

    /**
     * Inner class defining the table
     */
    public static final class LibraryEntry implements BaseColumns {

        /**
         * String constants used in getType() function in the content provider class
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIBRARY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIBRARY;

        /**
         * Uri used to operate on this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LIBRARY);

        // the name of the table
        public static final String TABLE_NAME = "library";

        /**
         * Column id
         * type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Book title
         * type: INTEGER AUTOINCREMENT
         */
        public static final String COLUMN_TITLE = "title";
        /**
         * Book authors
         * type: TEXT NOT NULL
         */
        public static final String COLUMN_AUTHORS = "authors";
        /**
         * Book publisher
         * type: TEXT NOT NULL
         */
        public static final String COLUMN_PUBLISHER = "publisher";
        /**
         * Book published date
         * type: TEXT
         */
        public static final String COLUMN_PUBDATE = "pubdate";
        /**
         * Book description
         */
        public static final String COLUMN_DESCRIPTION = "description";
        /**
         * Book online link
         * type: TEXT
         */
        public static final String COLUMN_LINK = "link";
        /**
         * Book thumbnail
         * type: BLOB
         */
        public static final String COLUMN_THUMB = "thumb";
        /**
         * Book id
         * type: TEXT NOT NULL
         */
        public static String COLUMN_BOOKID = "bookid";
    }
}

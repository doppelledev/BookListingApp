package com.example.android.booklistingapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.android.booklistingapp.data.LibraryContract.LibraryEntry;


public class LibraryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static LibraryCursorAdapter mAdapter;
    public static int LOADER_ID = 0;

    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        mAdapter = new LibraryCursorAdapter(getActivity(), null);
        ListView listView = rootView.findViewById(R.id.library_list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Book book = getBook(id);
                Intent intent = new Intent(getActivity(), BookActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return rootView;
    }

    private Book getBook(long id) {
        Uri uri = ContentUris.withAppendedId(LibraryEntry.CONTENT_URI, id);
        Cursor cursor = getContext().getContentResolver().query(uri, null, null,
                null, null);
        Book book = null;
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                String bookid = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_BOOKID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_TITLE));
                String authors = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_AUTHORS));
                String publisher = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_PUBLISHER));
                String pubdate = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_PUBDATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_DESCRIPTION));
                byte [] thumbBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_THUMB));
                String link = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_LINK));
                book = new Book(bookid, title, authors, publisher, pubdate, description, thumbBytes, link);
            }
            cursor.close();
        }
        return book;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_dummy:
                insertDummy();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertDummy() {
        ContentValues values = new ContentValues();
        values.put(LibraryEntry.COLUMN_BOOKID, "#########");
        values.put(LibraryEntry.COLUMN_TITLE, "Game of Throne");
        values.put(LibraryEntry.COLUMN_AUTHORS, "Jake Paul");
        getContext().getContentResolver().insert(LibraryEntry.CONTENT_URI, values);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        String [] projection = {
                LibraryEntry._ID,
                LibraryEntry.COLUMN_BOOKID,
                LibraryEntry.COLUMN_TITLE,
                LibraryEntry.COLUMN_AUTHORS,
                LibraryEntry.COLUMN_PUBLISHER,
                LibraryEntry.COLUMN_PUBDATE,
                LibraryEntry.COLUMN_DESCRIPTION,
                LibraryEntry.COLUMN_LINK,
                LibraryEntry.COLUMN_THUMB};
        return new CursorLoader(getContext(), LibraryEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

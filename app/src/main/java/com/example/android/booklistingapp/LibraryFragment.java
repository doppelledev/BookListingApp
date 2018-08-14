package com.example.android.booklistingapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        getLoaderManager().initLoader(LOADER_ID, null, this);

        return rootView;
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

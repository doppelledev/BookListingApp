package com.example.android.booklistingapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.booklistingapp.data.LibraryContract.LibraryEntry;

public class LibraryCursorAdapter extends CursorAdapter {

    public LibraryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title_tv = view.findViewById(R.id.title);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_TITLE));
        title_tv.setText(title);
        TextView authors_tv = view.findViewById(R.id.authors);
        String authors = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_AUTHORS));
        authors_tv.setText(authors);
        TextView pubdate_tv = view.findViewById(R.id.published_date);
        String pubdate = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_PUBDATE));
        pubdate = pubdate.length() >= 5 ?
                pubdate.substring(0, 4) : context.getResources().getString(R.string.notFound);
        pubdate_tv.setText(pubdate);
    }
}

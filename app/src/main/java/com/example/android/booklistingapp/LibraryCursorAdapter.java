package com.example.android.booklistingapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.booklistingapp.data.LibraryContract.LibraryEntry;

public class LibraryCursorAdapter extends CursorAdapter {

    LibraryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title_tv = view.findViewById(R.id.list_item_title);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_TITLE));
        title_tv.setText(title);
        TextView authors_tv = view.findViewById(R.id.list_item_authors);
        String authors = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_AUTHORS));
        authors_tv.setText(authors);
        TextView pubdate_tv = view.findViewById(R.id.list_item_pubdate);
        String pubdate = cursor.getString(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_PUBDATE));
        pubdate = pubdate != null && pubdate.length() >= 4 ?
                pubdate.substring(0, 4) : context.getResources().getString(R.string.notFound);
        pubdate_tv.setText(pubdate);
        ImageView thumb = view.findViewById(R.id.list_item_thumb);
        byte[] thumbBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(LibraryEntry.COLUMN_THUMB));
        Bitmap thumbBitmap = BitmapUtils.getBitmap(thumbBytes);
        thumb.setImageBitmap(thumbBitmap);
    }
}

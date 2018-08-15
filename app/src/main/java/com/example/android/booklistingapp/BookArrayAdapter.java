package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Class used in SearchFragment
 */
public class BookArrayAdapter extends ArrayAdapter {

    private ArrayList<Book> books;

    public BookArrayAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
        this.books = books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        Book book = books.get(position);

        TextView title_tv = convertView.findViewById(R.id.list_item_title);
        title_tv.setText(book.getTitle());

        TextView authors_tv = convertView.findViewById(R.id.list_item_authors);
        authors_tv.setText(book.getAuthors());

        TextView publishedDate_tv = convertView.findViewById(R.id.list_item_pubdate);
        String publishedDate = book.getPublishedDate();
        // only display the year of the date if available
        publishedDate = publishedDate.length() >= 4 ?
                publishedDate.substring(0, 4) : getContext().getResources().getString(R.string.notFound);
        publishedDate_tv.setText(publishedDate);

        ImageView thumb = convertView.findViewById(R.id.list_item_thumb);
        thumb.setVisibility(View.GONE);

        return convertView;
    }

    @Nullable
    @Override
    public Book getItem(int position) {
        return books.get(position);
    }
}

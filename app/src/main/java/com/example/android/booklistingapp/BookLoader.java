package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {
    private String search_query, previous_search_query;
    private ProgressBar progressBar;

    public BookLoader(Context context, String search_query, ProgressBar progressBar) {
        super(context);
        this.search_query = search_query;
        this.progressBar = progressBar;
    }

    @Override
    protected void onStartLoading() {
        // we check if the search query is the same as the one used in the previous search
        // if it is, we don't query the search
        // If we don't check, the same search request will be sent again if the user
        // goes back from another activity
        // and we would see duplicated results
        if (!search_query.equals(previous_search_query)) {
            previous_search_query = search_query;
            progressBar.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    @Nullable
    @Override
    public ArrayList<Book> loadInBackground() {
        return QueryUtils.searchBooks(search_query);
    }
}

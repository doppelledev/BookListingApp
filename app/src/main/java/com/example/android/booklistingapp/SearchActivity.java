package com.example.android.booklistingapp;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Book>>{
    public static final int LOADER_ID = 0;
    public static boolean first = true;
    public BookAdapter mAdapter;
    public ListView listView;
    public ProgressBar progressBar;
    public EditText searchEdit;
    public View searchButton;

    public View emptyView;
    public ImageView emptyImage;
    public TextView emptyTitle;
    public TextView emptySubTitle;
    public ImageView startingImage;

    public static final int NO_RESULTS = 0;
    public static final int NO_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = findViewById(R.id.list);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, BookActivity.class);
                intent.putExtra("Book", (Book)adapterView.getAdapter().getItem(i));
                startActivity(intent);
            }
        });
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        searchEdit = findViewById(R.id.search_edit);
        searchEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                    return true;
                }
                return false;
            }
        });
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        emptyView = findViewById(R.id.empty_view);
        emptyImage = findViewById(R.id.empty_image);
        emptyTitle = findViewById(R.id.empty_title);
        emptySubTitle = findViewById(R.id.empty_subtitle);
        startingImage = findViewById(R.id.starting_image);

        Bundle b = new Bundle();
        b.putString("query", "");
        getSupportLoaderManager().initLoader(LOADER_ID, b, this);
    }

    public void search(){
        // Hide keyboard when the user finishes typing
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);

        mAdapter.clear();
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            setEmptyView(NO_INTERNET);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("query", searchEdit.getText().toString());
        getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public android.support.v4.content.Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        if (!first) {
            startingImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        setEmptyView(View.GONE);
        return new BookLoader(this, bundle.getString("query"), progressBar);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        progressBar.setVisibility(View.GONE);
        if (books != null) {
            mAdapter.addAll(books);
            setEmptyView(View.GONE);
        }
        else if (!first) {
            setEmptyView(NO_RESULTS);
        }
        first = false;
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<ArrayList<Book>> loader) {
        mAdapter.clear();
    }

    public void setEmptyView(int mode){
        switch (mode) {
            case View.GONE:
                emptyView.setVisibility(View.GONE);
                break;
            case NO_RESULTS:
                emptyImage.setImageResource(R.drawable.open_book);
                emptyTitle.setText(getString(R.string.no_results));
                emptySubTitle.setText(getString(R.string.try_again));
                emptyView.setVisibility(View.VISIBLE);
                break;
            case NO_INTERNET:
                emptyImage.setImageResource(R.drawable.no_inernet);
                emptyTitle.setText(getString(R.string.no_internet));
                emptySubTitle.setText("");
                emptyView.setVisibility(View.VISIBLE);
        }
    }
}

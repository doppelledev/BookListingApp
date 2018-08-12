package com.example.android.booklistingapp;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Book>>{
    public static final int LOADER_ID = 0;
    public static boolean first = true;
    public BookAdapter mAdapter;
    public TextView emptyView;
    public ProgressBar progressBar;
    public EditText searchEdit;
    public View searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        emptyView = findViewById(R.id.empty);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, BookActivity.class);
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
            emptyView.setText(getResources().getString(R.string.noInternet));
            return;
        }
        emptyView.setText("");
        Bundle bundle = new Bundle();
        bundle.putString("query", searchEdit.getText().toString());
        getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);

    }

    @Override
    public android.support.v4.content.Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, bundle.getString("query"), progressBar);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        if (!first)
            emptyView.setText(getResources().getString(R.string.noBooksFound));
        first = false;
        progressBar.setVisibility(View.GONE);
        if (books != null)
            mAdapter.addAll(books);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<ArrayList<Book>> loader) {
        Log.d("woops", "onLaoderReset()");
        mAdapter.clear();
    }
}

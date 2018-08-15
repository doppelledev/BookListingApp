package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Book>> {

    public static final int LOADER_ID = 0;
    public static boolean first = true;
    public BookArrayAdapter mAdapter;
    public ListView listView;
    public ProgressBar progressBar;
    public EditText searchEdit;
    public View searchButton;

    public View emptyView;
    public ImageView emptyImage;
    public TextView emptyTitle;
    public TextView emptySubTitle;

    public static final int NO_RESULTS = 0;
    public static final int NO_INTERNET = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        listView = rootView.findViewById(R.id.list);
        mAdapter = new BookArrayAdapter(getContext(), new ArrayList<Book>());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), BookActivity.class);
                intent.putExtra("Book", (Book)adapterView.getAdapter().getItem(i));
                startActivity(intent);
            }
        });
        progressBar = rootView.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        searchEdit = rootView.findViewById(R.id.search_edit);
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
        searchButton = rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        emptyView = rootView.findViewById(R.id.search_emptyView);
        emptyImage = rootView.findViewById(R.id.search_emptyImage);
        emptyTitle = rootView.findViewById(R.id.search_emptyTitle);
        emptySubTitle = rootView.findViewById(R.id.search_emptySubtitle);

        Bundle b = new Bundle();
        b.putString("query", "");
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, b, this);

        return rootView;
    }

    public void search(){
        // Hide keyboard when the user finishes typing
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);

        String query = searchEdit.getText().toString();
        if (TextUtils.isEmpty(query))
            return;

        mAdapter.clear();
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            setEmptyView(NO_INTERNET);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public android.support.v4.content.Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        if (!first) {
            progressBar.setVisibility(View.VISIBLE);
        }
        setEmptyView(View.GONE);
        return new BookAsyncTaskLoader(getContext(), bundle.getString("query"), progressBar);
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

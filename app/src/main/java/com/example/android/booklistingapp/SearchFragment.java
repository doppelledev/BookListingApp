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
    public static final int NO_RESULTS = 0;
    public static final int NO_INTERNET = 1;
    public static boolean first = true; // First time searching?
    public BookArrayAdapter mAdapter;
    public ListView listView;
    public ProgressBar progressBar;
    public EditText searchEdit;
    public View searchButton;
    public View emptyView;
    public ImageView emptyImage;
    public TextView emptyTitle;
    public TextView emptySubTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        listView = rootView.findViewById(R.id.search_list);
        mAdapter = new BookArrayAdapter(getContext(), new ArrayList<Book>());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), BookActivity.class);
                intent.putExtra("Book", (Book) adapterView.getAdapter().getItem(i));
                startActivity(intent);
            }
        });
        progressBar = rootView.findViewById(R.id.search_progress);
        progressBar.setVisibility(View.GONE);
        searchEdit = rootView.findViewById(R.id.search_edit);
        searchEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        // Make it so the search function is called if the user clicks on 'done' from the keyboard
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
        // Instead of setting an empty view using .setEmptyView(), we manipulate the views below
        //  depending on the situation.
        // Two empty views will be displayed according to the case (No internet / no search results)
        emptyView = rootView.findViewById(R.id.search_emptyView);
        emptyImage = rootView.findViewById(R.id.search_emptyImage);
        emptyTitle = rootView.findViewById(R.id.search_emptyTitle);
        emptySubTitle = rootView.findViewById(R.id.search_emptySubtitle);

        // initialise the loader (found out that restartLoader() won't work
        // if we didn't call initLoader() first)
        Bundle b = new Bundle();
        b.putString("query", "");
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, b, this);

        return rootView;
    }

    // This function is called when the user clicks the search button
    public void search() {
        // Hide keyboard when the user finishes typing
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);

        // Get search query entered by the user
        String query = searchEdit.getText().toString();
        if (TextUtils.isEmpty(query))
            // if it's empty, we don't query the search and just return
            return;

        // if it's not empty, we discard the previous results
        mAdapter.clear();
        // and then check if there's connectivity
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            // if there isn't, we set the appropriate empty view and return
            setEmptyView(NO_INTERNET);
            return;
        }
        // if there is, we perform the search
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public android.support.v4.content.Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        progressBar.setVisibility(View.VISIBLE);
        setEmptyView(View.GONE);
        return new BookAsyncTaskLoader(getContext(), bundle.getString("query"), progressBar);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        progressBar.setVisibility(View.GONE);
        if (books != null)
            mAdapter.addAll(books);
        else if (!first)
            // if this is the first time a search is being performed, it means that it's from
            // calling initLoader(). We don't want to display the empty view because the user
            // didn't perform any search.
            setEmptyView(NO_RESULTS);
        first = false;
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<ArrayList<Book>> loader) {
        mAdapter.clear();
    }

    // Setting the empty view accordingly to the scenario
    public void setEmptyView(int mode) {
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

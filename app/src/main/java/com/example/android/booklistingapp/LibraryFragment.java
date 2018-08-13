package com.example.android.booklistingapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {


    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        ListView listView = rootView.findViewById(R.id.library_list);
        ArrayList<Book> books = new ArrayList<>();
        books.add(new Book("Game of Thrones", "Jake Paul", "National geographic",
                "2018", "Medieval shit", "", "www.google.com"));
        BookAdapter adapter = new BookAdapter(getContext(), books, BookAdapter.MODE_LIBRARY);
        listView.setAdapter(adapter);

        return rootView;
    }

}

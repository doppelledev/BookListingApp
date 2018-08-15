package com.example.android.booklistingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.booklistingapp.data.LibraryContract.LibraryEntry;

import java.lang.ref.WeakReference;
import java.net.URL;

public class BookActivity extends AppCompatActivity {
    public Book book;
    String bookid;
    public ImageView thumb;
    public static Bitmap thumbBitmap;
    public boolean bookExists;
    public Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        thumb = findViewById(R.id.s_thumb);
        book = (Book)getIntent().getSerializableExtra("Book");
        if (book.getThumbUrl() != null) {
            getThumb task = new getThumb(this);
            task.execute(book.getThumbUrl());
            updateImage();
        } else {
            thumbBitmap = BitmapUtils.getBitmap(book.getThumbBytes());
            updateImage();
        }

        String field;
        String notFound = getResources().getString(R.string.notFound);
        bookid = book.getId();
        TextView title = findViewById(R.id.s_title);
        title.setText(book.getTitle());
        TextView authors = findViewById(R.id.s_authors);
        authors.setText(book.getAuthors());
        TextView publisher = findViewById(R.id.s_publisher);
        field = book.getPublisher();
        publisher.setText((field != null && !field.equals("")) ? field : notFound);
        TextView publishedDate = findViewById(R.id.s_published_date);
        field = book.getPublishedDate();
        publishedDate.setText((field != null && !field.equals("")) ? field : notFound);
        TextView description = findViewById(R.id.s_description);
        field = book.getDescription();
        description.setText((field != null && !field.equals("")) ? field : notFound);

        FrameLayout viewOnline = findViewById(R.id.view_online);
        viewOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book.getLink() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(book.getLink()));
                    startActivity(intent);
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.linkNotAvailable),
                            Toast.LENGTH_SHORT).show();
            }
        });

        String selection = LibraryEntry.COLUMN_BOOKID + "=?";
        String [] selectionArgs = {book.getId()};
        Cursor cursor = getContentResolver().query(LibraryEntry.CONTENT_URI, null,
                selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() == 0)
                bookExists = true;
            cursor.close();
        }
        else
            bookExists = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (bookExists) {
            getMenuInflater().inflate(R.menu.menu_save, menu);
            this.menu = menu;
            return true;
        } else {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save_book:
                saveBook();
                return true;
            case R.id.delete_book:
                deleteBook();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBook() {
        ContentValues values = new ContentValues();
        values.put(LibraryEntry.COLUMN_BOOKID, book.getId());
        values.put(LibraryEntry.COLUMN_TITLE, book.getTitle());
        values.put(LibraryEntry.COLUMN_AUTHORS, book.getAuthors());
        values.put(LibraryEntry.COLUMN_PUBLISHER, book.getPublisher());
        values.put(LibraryEntry.COLUMN_PUBDATE, book.getPublishedDate());
        values.put(LibraryEntry.COLUMN_DESCRIPTION, book.getDescription());
        values.put(LibraryEntry.COLUMN_LINK, book.getLink());
        byte [] thumbBytes = BitmapUtils.getBytes(thumbBitmap);
        if (thumbBytes == null) {
            // if the book has no thumbnail, set the thumbnail to a default image
            Bitmap image404 = BitmapFactory.decodeResource(getResources(), R.drawable.image404);
            thumbBytes = BitmapUtils.getBytes(image404);
        }
        values.put(LibraryEntry.COLUMN_THUMB, thumbBytes);
        Uri newUri = getContentResolver().insert(LibraryEntry.CONTENT_URI, values);
        if (newUri == null)
            Toast.makeText(this, R.string.not_saved, Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            menu.findItem(R.id.save_book).setVisible(false);
        }
    }

    public void deleteBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.sureness))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selection = LibraryEntry.COLUMN_BOOKID + "=?";
                        String [] selectionArgs = {bookid};
                        int rowsDeleted =
                                getContentResolver().delete(LibraryEntry.CONTENT_URI, selection, selectionArgs);
                        if (rowsDeleted > 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.deleted),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.not_deleted),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null)
                            dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static class getThumb extends AsyncTask<String, Void, Bitmap> {

        WeakReference<Activity> mWeakReference;

        getThumb(Activity activity) {
            super();
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Activity activity = mWeakReference.get();
            ImageView thumb= activity.findViewById(R.id.s_thumb);
            if (bitmap != null)
                thumb.setImageBitmap(bitmap);
            else
                thumb.setImageResource(R.drawable.image404);
            thumbBitmap = bitmap;
        }
    }

    public void updateImage(){
        if (thumbBitmap != null)
            thumb.setImageBitmap(thumbBitmap);
        else
            thumb.setImageResource(R.drawable.image404);
    }

}

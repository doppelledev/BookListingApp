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
    public static Bitmap thumbBitmap;
    public Book book;
    public ImageView thumb;
    public boolean bookExists;
    public Menu menu;
    String bookid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        thumb = findViewById(R.id.b_thumb);
        book = (Book) getIntent().getSerializableExtra("Book");

        if (book.getThumbUrl() != null) {
            // the book has only the thumbnail url
            // we need to download it
            getThumb task = new getThumb(this);
            task.execute(book.getThumbUrl());
            updateImage();
        } else {
            // the book has its thumbnail downloaded, so we use it directly
            // this bloc is executed when the intent is sent from the LibraryFragment
            // where we used thumbnail byte[] to construct the book object
            thumbBitmap = BitmapUtils.getBitmap(book.getThumbBytes());
            updateImage();
        }

        // set the activity title to be the title of the book
        setTitle(book.getTitle());

        // populate layout views with book information
        String field;
        String notFound = getResources().getString(R.string.notFound);
        bookid = book.getId();
        TextView title = findViewById(R.id.b_title);
        title.setText(book.getTitle());
        TextView authors = findViewById(R.id.b_authors);
        authors.setText(book.getAuthors());
        TextView publisher = findViewById(R.id.b_publisher);
        field = book.getPublisher();
        publisher.setText((field != null && !field.equals("")) ? field : notFound);
        TextView publishedDate = findViewById(R.id.b_pubdate);
        field = book.getPublishedDate();
        publishedDate.setText((field != null && !field.equals("")) ? field : notFound);
        TextView description = findViewById(R.id.b_description);
        field = book.getDescription();
        description.setText((field != null && !field.equals("")) ? field : notFound);

        // set onclick listener for when the user clicks "View it online" 'button'
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

        // we query the database using the book's unique id 'bookid"
        // to see if it already exists, and set the boolean 'bookExists' accordingly
        // the boolean is used to determine whether we display a save or delete button in the actionbar
        String selection = LibraryEntry.COLUMN_BOOKID + "=?";
        String[] selectionArgs = {book.getId()};
        Cursor cursor = getContentResolver().query(LibraryEntry.CONTENT_URI, null,
                selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() == 0)
                bookExists = false;
            else
                bookExists = true;
            cursor.close();
        } else
            bookExists = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // we save the menu for later use
        this.menu = menu;
        if (bookExists) {
            // if the book exists, we display the deleted button
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        } else {
            // if not, we display the save button
            getMenuInflater().inflate(R.menu.menu_save, menu);
            return true;
        }
    }

    // implementing actionbar's buttons clicks
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
        // put all bok information in the ContentValues
        ContentValues values = new ContentValues();
        values.put(LibraryEntry.COLUMN_BOOKID, book.getId());
        values.put(LibraryEntry.COLUMN_TITLE, book.getTitle());
        values.put(LibraryEntry.COLUMN_AUTHORS, book.getAuthors());
        values.put(LibraryEntry.COLUMN_PUBLISHER, book.getPublisher());
        values.put(LibraryEntry.COLUMN_PUBDATE, book.getPublishedDate());
        values.put(LibraryEntry.COLUMN_DESCRIPTION, book.getDescription());
        values.put(LibraryEntry.COLUMN_LINK, book.getLink());
        byte[] thumbBytes = BitmapUtils.getBytes(thumbBitmap);
        if (thumbBytes == null) {
            // if the book has no thumbnail, set the thumbnail to a default image
            Bitmap image404 = BitmapFactory.decodeResource(getResources(), R.drawable.image404);
            thumbBytes = BitmapUtils.getBytes(image404);
        }
        values.put(LibraryEntry.COLUMN_THUMB, thumbBytes);

        // insertion
        Uri newUri = getContentResolver().insert(LibraryEntry.CONTENT_URI, values);
        if (newUri == null)
            // book hasn't been saved
            Toast.makeText(this, R.string.not_saved, Toast.LENGTH_SHORT).show();
        else {
            // book has been saved
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            // we remove the add button, and display the delete button
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
    }

    public void deleteBook() {
        // show an alert dialog to confirm the deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.sureness))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // user is sure about the deletion
                        // set selection and selectionArgs to point to this book in the database
                        // using its unique id
                        String selection = LibraryEntry.COLUMN_BOOKID + "=?";
                        String[] selectionArgs = {bookid};
                        // deletion
                        int rowsDeleted =
                                getContentResolver().delete(LibraryEntry.CONTENT_URI, selection, selectionArgs);
                        if (rowsDeleted > 0) {
                            // the book has been deleted
                            Toast.makeText(getApplicationContext(), getString(R.string.deleted),
                                    Toast.LENGTH_SHORT).show();
                            // remove the delete button and show the save button
                            menu.clear();
                            getMenuInflater().inflate(R.menu.menu_save, menu);
                        } else
                            // the book hasn't been deleted
                            Toast.makeText(getApplicationContext(), getString(R.string.not_deleted),
                                    Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // user is not sure about deletion
                        // hide the dialog
                        if (dialogInterface != null)
                            dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // to update the image if the thumbnail already existed in the book object
    public void updateImage() {
        if (thumbBitmap != null)
            thumb.setImageBitmap(thumbBitmap);
        else
            thumb.setImageResource(R.drawable.image404);
    }

    /**
     * Inner class that downloads the book's thumbnail in a separate thread
     */
    private static class getThumb extends AsyncTask<String, Void, Bitmap> {
        // a reference to the activity
        // used to set the imageView bitmap after download
        WeakReference<Activity> mWeakReference;

        getThumb(Activity activity) {
            super();
            mWeakReference = new WeakReference<>(activity);
        }

        // performing the download
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

        // download is finished
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // dereference the activity
            Activity activity = mWeakReference.get();
            // get the image view
            ImageView thumb = activity.findViewById(R.id.b_thumb);
            if (bitmap != null)
                // if the image has been downloaded, display it in the image view
                thumb.setImageBitmap(bitmap);
            else
                // if not, display a default image
                thumb.setImageResource(R.drawable.image404);
            // store the thumbnail for later use
            thumbBitmap = bitmap;
        }
    }

}

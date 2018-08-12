package com.example.android.booklistingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URL;

public class BookActivity extends AppCompatActivity {
    public Book book;
    public ImageView thumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        book = (Book)getIntent().getSerializableExtra("Book");

        thumb = findViewById(R.id.s_thumb);
        getThumb task = new getThumb();
        task.execute(book.getThumbUrl());

        String field;
        String notFound = getResources().getString(R.string.notFound);
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
    }

    private class getThumb extends AsyncTask<String, Void, Bitmap> {

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
            if (bitmap != null)
                thumb.setImageBitmap(bitmap);
            else
                thumb.setImageResource(R.drawable.image404);
        }
    }

}

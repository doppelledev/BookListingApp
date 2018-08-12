package com.example.android.booklistingapp;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Book implements Serializable{
    private String title;
    private String authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private String thumbUrl;
    private String link;

    public Book(String title, String authors, String publisher,
                String publishedDate, String description, String thumbUrl,  String link){
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbUrl = thumbUrl;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "Title: " + getTitle() + "\nAuthor: " + getAuthors() + "\nPublished date: " + getPublishedDate();
    }
}

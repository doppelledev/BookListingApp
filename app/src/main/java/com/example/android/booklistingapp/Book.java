package com.example.android.booklistingapp;

import java.io.Serializable;

public class Book implements Serializable {
    private String id;              // unique book identifier, different from _ID in the database
    private String title;
    private String authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private String thumbUrl;        // url of the book's thumbnail
    private byte[] thumbBytes;     // book thumbnail converted to byte[]
    private String link;            // link to view the book online

    // first constructor, used in QueryUtils where the thumbnails are nto downloaded
    // thus oly thumbUrl is provided
    Book(String id, String title, String authors, String publisher,
         String publishedDate, String description, String thumbUrl, String link) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbUrl = thumbUrl;
        this.link = link;
    }

    // second constructor, used when we have the actual thumbnail downloaded
    // we provide the thumbnail as a byte[]
    Book(String id, String title, String authors, String publisher,
         String publishedDate, String description, byte[] thumbBytes, String link) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbBytes = thumbBytes;
        this.link = link;
    }

    // typical getters

    public byte[] getThumbBytes() {
        return thumbBytes;
    }

    public String getId() {
        return id;
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

    // to string method, helps in debugging
    @Override
    public String toString() {
        return "Title: " + getTitle() + "\nAuthor: " + getAuthors() + "\nPublished date: " + getPublishedDate();
    }
}

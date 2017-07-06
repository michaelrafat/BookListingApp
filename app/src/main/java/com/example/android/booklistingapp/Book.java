package com.example.android.booklistingapp;


/**
 * Created by miche on 6/25/2017.
 */

public class Book {

    private String title;
    private String author;
    private String publisher;
    private String image;

    public Book(String bookName, String author, String publisher, String image) {
        this.title = bookName;
        this.author = author;
        this.publisher = publisher;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
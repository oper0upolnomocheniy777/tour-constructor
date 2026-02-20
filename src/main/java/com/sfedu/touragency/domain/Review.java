package com.sfedu.touragency.domain;

import java.util.*;

public class Review {
    private Long id;

    private String text;

    private int rating;

    private Date date;

    private User author;

    private Tour tour;

    public Review() {
    }

    public Review(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return rating == review.rating &&
                Objects.equals(id, review.id) &&
                Objects.equals(text, review.text) &&
                Objects.equals(date, review.date) &&
                Objects.equals(author, review.author) &&
                Objects.equals(tour, review.tour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, rating, date, author, tour);
    }

    @Override
    public String toString() {
        return "Review{" +
                "author=" + author +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", rating=" + rating +
                ", date=" + date +
                ", tour=" + tour +
                '}';
    }
}

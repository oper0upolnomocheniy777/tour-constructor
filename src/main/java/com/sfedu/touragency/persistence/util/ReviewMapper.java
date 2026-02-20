package com.sfedu.touragency.persistence.util;

import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.User;

import java.sql.*;

public class ReviewMapper {
    public static Review map(ResultSet rs) throws SQLException {
        Review review = new Review();

        review.setId(rs.getLong("id"));
        review.setText(rs.getString("text"));
        review.setRating(rs.getInt("rating"));
        review.setAuthor(new User(rs.getLong("author_id")));
        review.setTour(new Tour(rs.getLong("tour_id")));
        review.setDate(rs.getDate("date"));

        return review;
    }
}

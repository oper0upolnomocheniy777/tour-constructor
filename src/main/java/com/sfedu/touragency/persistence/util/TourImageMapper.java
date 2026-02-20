package com.sfedu.touragency.persistence.util;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.TourImage;

import java.net.*;
import java.sql.*;

public class TourImageMapper {
    public static TourImage map(ResultSet rs) throws SQLException {
        TourImage tourImage = new TourImage();

        tourImage.setId(rs.getLong("id"));
        tourImage.setTour(new Tour(rs.getLong("tour_id")));
        try {
            tourImage.setImageUrl(new URL(rs.getString("image_url")));
        } catch (MalformedURLException | SQLException e) {
            tourImage.setImageUrl(null);
        }
        try {
            tourImage.setThumbnailUrl(new URL(rs.getString("thumbnail_url")));
        } catch (MalformedURLException | SQLException e) {
            tourImage.setThumbnailUrl(null);
        }

        return tourImage;
    }
}

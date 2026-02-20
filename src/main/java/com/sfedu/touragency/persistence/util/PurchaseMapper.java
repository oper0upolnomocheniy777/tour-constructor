package com.sfedu.touragency.persistence.util;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.domain.PurchaseStatus;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.User;

import java.sql.*;

public class PurchaseMapper {
    public static Purchase map(ResultSet rs) throws SQLException {
        Purchase purchase = new Purchase();
        purchase.setId(rs.getLong("id"));
        purchase.setUser(new User(rs.getLong("user_id")));
        purchase.setTour(new Tour(rs.getLong("tour_id")));
        purchase.setPrice(rs.getBigDecimal("price"));
        purchase.setDate(rs.getDate("date"));
        purchase.setStatus(PurchaseStatus.values()[rs.getInt("status")]);
        return purchase;
    }
}

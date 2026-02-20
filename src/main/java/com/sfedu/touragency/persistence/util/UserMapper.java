package com.sfedu.touragency.persistence.util;

import com.sfedu.touragency.domain.User;

import java.sql.*;

public class UserMapper {
    public static User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setPassword(rs.getString("password"));
        user.setDiscount(rs.getInt("discount"));
        user.setTelephone(rs.getString("telephone"));
        return user;
    }
}

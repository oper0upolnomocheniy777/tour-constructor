package com.sfedu.touragency.persistence.dao.jdbc;

import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.JdbcTemplate;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.persistence.util.UserMapper;

import java.math.*;
import java.util.*;

public class UserJdbcDao implements UserDao {
    private ConnectionManager connectionManager;

    private JdbcTemplate jdbcTemplate;

    public UserJdbcDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public Long create(User u) {
        Long id = jdbcTemplate.insert("INSERT INTO `user` (`username`, `firstName`, " +
                "`lastName`, `password`, `discount`, `telephone`)" +
                " VALUES (?, ?, ?, ?, ?, ?)", u.getUsername(), u.getFirstName(),
                u.getLastName(), u.getPassword(), u.getDiscount(), u.getTelephone());

        addRoles(id, u.getRoles());
        return id;
    }

    @Override
    public User read(Long id) {
        User user = jdbcTemplate.queryObject("SELECT * FROM `user` WHERE id=?",
                UserMapper::map, id);
        if (user != null) {
            user.setRoles(readRoles(user.getId()));
        }

        return user;
    }

    @Override
    public User read(String username) {
        User user = jdbcTemplate.queryObject("SELECT * FROM `user` WHERE username=?",
                UserMapper::map, username);

        if (user == null)
            return null;

        user.setRoles(readRoles(user.getId()));
        return user;
    }

    @Override
    public void update(User u) {
        jdbcTemplate.update("UPDATE `user` SET `username`=?, `firstName`=?," +
                " `lastName`=?, `password`=?, `discount`=?, `telephone`=? WHERE `id`=?",
                u.getUsername(), u.getFirstName(), u.getLastName(),
                u.getPassword(), u.getDiscount(), u.getTelephone(), u.getId());

        updateRoles(u.getId(), u.getRoles());
    }

    @Override
    public void delete(Long id) {
        deleteRoles(id);
        jdbcTemplate.update("DELETE FROM `user` WHERE id=?", id);
    }

    @Override
    public void addRoles(Long userId, List<Role> roles) {
        roles.forEach(role -> addRole(userId, role));
    }

    @Override
    public void addRole(Long userId, Role role) {
        jdbcTemplate.insert("INSERT INTO `user_role` (`user_id`, `role_id`) " +
                "VALUES (?, ?)", userId, role.ordinal() + 1);
    }

    @Override
    public void deleteRoles(Long userId) {
        jdbcTemplate.update("DELETE FROM `user_role` WHERE user_id=?", userId);
    }

    @Override
    public void updateRoles(Long userId, List<Role> roles) {
        deleteRoles(userId);
        addRoles(userId, roles);
    }

    @Override
    public List<Role> readRoles(Long userId) {
        return jdbcTemplate.queryObjects("SELECT role_id FROM `user_role` " +
                "WHERE `user_id`=?", (rs) -> Role.values()[rs.getInt("role_id") - 1],
                userId);
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.queryObjects("SELECT * FROM `user`",
                UserMapper::map);
        users.forEach(u -> u.setRoles(readRoles(u.getId())));
        return users;
    }

    @Override
    public int countPurchases(Long userId) {
        return jdbcTemplate.queryObjects("SELECT COUNT(*) FROM `purchase` " +
                "JOIN `user` ON `user`.id = `purchase`.user_id WHERE `user`.id=?",
                (rs) -> rs.getInt(1), userId).get(0);
    }

    @Override
    public BigDecimal computePurchasesTotalPrice(Long userId) {
        BigDecimal ans = jdbcTemplate.queryObjects("SELECT SUM(price) FROM `purchase` " +
                "JOIN `user` ON `user`.id = `purchase`.user_id WHERE `user`.id=?",
                (rs) -> rs.getBigDecimal(1), userId).get(0);

        if (ans == null) {
            return new BigDecimal(0);
        }

        return ans;
    }

    @Override
    public List<User> findAllOrderByRegularity(boolean byTotalPrice) {
        if (byTotalPrice) {
            return jdbcTemplate.queryObjects("SELECT `user`.*, SUM(ifnull(price, 0)) " +
                    "AS total FROM `user` LEFT JOIN `purchase` ON `user`.id=`user_id` " +
                    "GROUP BY `user`.`id` ORDER BY total DESC", UserMapper::map);
        }

        return jdbcTemplate.queryObjects("SELECT `user`.*, COUNT(price) AS total " +
                "FROM `user` LEFT JOIN `purchase` ON `user`.id = `user_id` " +
                "GROUP BY `user`.`id` ORDER BY total DESC", UserMapper::map);
    }

}

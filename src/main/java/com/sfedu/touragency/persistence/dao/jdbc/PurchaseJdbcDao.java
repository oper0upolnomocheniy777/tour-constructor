package com.sfedu.touragency.persistence.dao.jdbc;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.domain.PurchaseStatus;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.JdbcTemplate;
import com.sfedu.touragency.persistence.dao.PurchaseDao;
import com.sfedu.touragency.persistence.util.PurchaseMapper;

import java.util.*;

public class PurchaseJdbcDao implements PurchaseDao {
    private ConnectionManager connectionManager;

    private JdbcTemplate jdbcTemplate;

    public PurchaseJdbcDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public Long create(Purchase p) {
        Integer status = p.getStatus() == null ? PurchaseStatus.ACTIVE.ordinal()
                : p.getStatus().ordinal();

        Long id = jdbcTemplate.insert("INSERT INTO `purchase` (`user_id`, `tour_id`, " +
                "`date`, `price`, `status`) VALUES (?, ?, ?, ?, ?)"
                , p.getUser().getId(), p.getTour().getId(), p.getDate()
                , p.getPrice(), status);

        return id;
    }

    @Override
    public Purchase read(Long id) {
        Purchase purchase = jdbcTemplate.queryObject("SELECT * FROM `purchase` " +
                "WHERE id=?", PurchaseMapper::map, id);
        return purchase;
    }

    @Override
    public List<Purchase> findByUser(Long userId) {
        return jdbcTemplate.queryObjects("SELECT * FROM `purchase`" +
                " WHERE user_id=?", PurchaseMapper::map, userId);
    }

    @Override
    public List<Purchase> findByUserTour(Long userId, Long tourId) {
        return jdbcTemplate.queryObjects("SELECT * FROM `purchase` WHERE `user_id`=? " +
                "AND `tour_id`=?", PurchaseMapper::map, userId, tourId);
    }

    @Override
    public void update(Purchase p) {
        Integer status = p.getStatus() == null ? PurchaseStatus.ACTIVE.ordinal()
                : p.getStatus().ordinal();

        jdbcTemplate.update("UPDATE `purchase` SET `user_id`=?, `tour_id`=?, `date`=?," +
                " `price`=?, `status`=? WHERE `id`=?", p.getUser().getId(),
                p.getTour().getId(), p.getDate(), p.getPrice(), status, p.getId());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM `purchase` WHERE id=?", id);
    }

    @Override
    public List<Purchase> findAll() {
        return jdbcTemplate.queryObjects("SELECT * FROM `purchase`" +
                " ORDER BY id DESC", PurchaseMapper::map);
    }

    @Override
    public List<Purchase> findByStatusOrderByDate(PurchaseStatus status) {
        return jdbcTemplate.queryObjects("SELECT * FROM `purchase` WHERE status=? " +
                "ORDER BY date DESC", PurchaseMapper::map, status);
    }
}

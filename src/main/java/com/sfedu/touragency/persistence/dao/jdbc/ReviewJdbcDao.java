package com.sfedu.touragency.persistence.dao.jdbc;

import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.JdbcTemplate;
import com.sfedu.touragency.persistence.dao.ReviewDao;
import com.sfedu.touragency.persistence.transaction.Transaction;
import com.sfedu.touragency.persistence.util.ReviewMapper;

import java.util.*;

public class ReviewJdbcDao implements ReviewDao {

    private ConnectionManager connectionManager;

    private JdbcTemplate jdbcTemplate;

    public ReviewJdbcDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public Long create(Review review) {

        Long id[] = {null};
        Transaction.tx(connectionManager, () -> {
            id[0] = jdbcTemplate.insert("INSERT INTO `review`(text, rating, date, " +
                            "author_id, tour_id) VALUES (?, ?, ?, ?, ?)", review.getText(),
                    review.getRating(), review.getDate(), review.getAuthor().getId(),
                    review.getTour().getId());

        });

        return id[0];
    }

    @Override
    public Review read(Long id) {
        return jdbcTemplate.queryObject("SELECT * FROM `review` WHERE id=?", ReviewMapper::map,
                id);
    }

    @Override
    public void update(Review review) {
        jdbcTemplate.update("UPDATE `review` SET text=?, rating=?, date=?, " +
                        "author_id=?, tour_id=? WHERE id=?", review.getText(), review.getRating(),
                review.getDate(), review.getAuthor().getId(), review.getTour().getId(),
                review.getId());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM `review` WHERE id=?", id);
    }

    @Override
    public List<Review> findAll() {
        return jdbcTemplate.queryObjects("SELECT * FROM `review` ORDER BY id DESC",
                ReviewMapper::map);
    }

    @Override
    public List<Review> findByTour(Long id) {
        return jdbcTemplate.queryObjects("SELECT * FROM `review` WHERE tour_id=? " +
                "ORDER BY id DESC", ReviewMapper::map,
                id);
    }

    @Override
    public boolean canVote(Long userId, Long tourId) {
        boolean flagWrapper[] = new boolean[]{false};
        jdbcTemplate.query("SELECT id FROM `review` WHERE author_id=? AND tour_id=?",
                rs -> flagWrapper[0] = !rs.next(), userId, tourId);

        jdbcTemplate.query("SELECT id FROM `purchase` WHERE user_id=? AND tour_id=?",
                rs -> flagWrapper[0] &= rs.next(), userId, tourId);

        return flagWrapper[0];
    }

    @Override
    public Review findByPurchase(Long userId, Long tourId) {
        return jdbcTemplate.queryObject("SELECT * FROM `review` WHERE author_id=? " +
                "AND tour_id=?", ReviewMapper::map, userId, tourId);
    }
}

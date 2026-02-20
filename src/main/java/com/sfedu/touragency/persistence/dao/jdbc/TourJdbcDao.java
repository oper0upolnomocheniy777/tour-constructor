package com.sfedu.touragency.persistence.dao.jdbc;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.JdbcTemplate;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.ToursDynamicFilter;
import com.sfedu.touragency.persistence.util.TourMapper;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class TourJdbcDao implements TourDao {


    private static final BigDecimal LARGE_DECIMAL = new BigDecimal(Long.MAX_VALUE);

    private ConnectionManager connectionManager;

    private JdbcTemplate jdbcTemplate;

    public TourJdbcDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public Long create(Tour t) {
        return jdbcTemplate.insert("INSERT INTO `tour` (`title`, `description`, `type`," +
                " `hot`, `price`, `enabled`, `avg_rating`, `votes_count`, `discount`, " +
                "`destination`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                t.getTitle(), t.getDescription(), t.getType().ordinal(), t.isHot(),
                t.getPrice(), t.isEnabled(), t.getAvgRating(), t.getVotesCount(),
                t.getDiscount(), t.getDestination());
    }

    @Override
    public Tour read(Long id) {
        return jdbcTemplate.queryObject("SELECT * FROM tour WHERE id=?",
                TourMapper::map, id);
    }

    @Override
    public void update(Tour t) {
        jdbcTemplate.update("UPDATE `tour` SET `title`=?, " +
                "`description`=?, `type`=?, `hot`=?, `price`=?, `enabled`=?,`avg_rating`=?," +
                "`votes_count`=?, `discount`=?, `destination`=? WHERE `id`=?",
                t.getTitle(), t.getDescription(), t.getType().ordinal(), t.isHot(),
                t.getPrice(), t.isEnabled(), t.getAvgRating(), t.getVotesCount(),
                t.getDiscount(), t.getDestination(), t.getId());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM tour WHERE id=?", id);
    }

    @Override
    public List<Tour> findAll() {
        return jdbcTemplate.queryObjects("SELECT * FROM tour",
                TourMapper::map);
    }

    @Override
    public BigDecimal computePrice(Long tourId, Long userId) {
        return jdbcTemplate.queryObjects("SELECT price*cast(" +
                  "(100 - GREATEST(user.discount, tour.discount))/100 as DECIMAL(10,2)" +
                ") FROM  `user`, tour WHERE `user`.id =? AND tour.id=?",
                (rs) -> rs.getBigDecimal(1), userId, tourId).get(0);
    }

    @Override
    public Tour findRandomHot() {
        return jdbcTemplate.queryObject("SELECT * FROM tour WHERE id >= " +
                "FLOOR(RAND()*(SELECT MAX(id) FROM tour)) " +
                " AND `hot`='1' LIMIT 1 ", TourMapper::map);
    }

    @Override
    public List<Tour> executeDynamicFilter(ToursDynamicFilter filter) {
        String[] params = new String[]{};
        if (filter.getSearchQuery() != null) {
            List<String> keywords = Arrays.asList(filter.getSearchQuery().split(" "));
            params = keywords.stream().flatMap(s -> Stream.of(s, s, s))
                    .collect(Collectors.toList()).toArray(params);
        }
        return jdbcTemplate.queryObjects(filter.getQuery(), TourMapper::map,
                (Object[]) params);
    }
}

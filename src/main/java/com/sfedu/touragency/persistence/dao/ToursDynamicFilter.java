package com.sfedu.touragency.persistence.dao;

import com.sfedu.touragency.domain.TourType;
import com.sfedu.touragency.persistence.query.builder.OrderByBuilder;
import com.sfedu.touragency.persistence.query.builder.QueryBuilder;
import com.sfedu.touragency.persistence.query.builder.WhereBuilder;
import com.sfedu.touragency.persistence.query.condition.LikeCondition;
import com.sfedu.touragency.util.SortDir;

import java.util.*;
import java.util.stream.*;

import static com.sfedu.touragency.persistence.query.Ordering.*;
import static com.sfedu.touragency.persistence.query.builder.WhereBuilder.*;

public class ToursDynamicFilter {
    private String searchQuery;

    private Integer priceLow;

    private Integer priceHigh;

    private EnumSet<TourType> tourTypes;

    private boolean hotFirst;

    private SortDir votesSort;

    private SortDir ratingSort;

    private SortDir priceSort;

    private Integer limit = null;

    private Integer offset = null;

    public ToursDynamicFilter() {
    }

    public ToursDynamicFilter setTourTypes(TourType... tourTypes) {
        if (tourTypes != null) {
            if (tourTypes.length == 0) {
                this.tourTypes = null;
            } else if (tourTypes.length == 1) {
                this.tourTypes = EnumSet.of(tourTypes[0]);
            } else {
                this.tourTypes = EnumSet.of(tourTypes[0], tourTypes);
            }
        }

        return this;
    }

    public ToursDynamicFilter setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public ToursDynamicFilter setRatingSort(SortDir ratingSort) {
        this.ratingSort = ratingSort;
        return this;
    }

    public ToursDynamicFilter setVotesSort(SortDir votesSort) {
        this.votesSort = votesSort;
        return this;
    }

    public ToursDynamicFilter setPriceSort(SortDir priceSort) {
        this.priceSort = priceSort;
        return this;
    }

    public ToursDynamicFilter setPriceLow(Integer priceLow) {
        this.priceLow = priceLow;
        return this;
    }

    public ToursDynamicFilter setPriceHigh(Integer priceHigh) {
        this.priceHigh = priceHigh;
        return this;
    }

    public ToursDynamicFilter setHotFirst(boolean hotFirst) {
        this.hotFirst = hotFirst;
        return this;
    }

    public ToursDynamicFilter setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ToursDynamicFilter setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public String getQuery() {
        WhereBuilder where = QueryBuilder.select("tour", "*");

        where = buildPrecondition(where);
        where = buildPriceLow(where);
        where = buildPriceHigh(where);
        where = buildTourTypes(where);
        where = buildSearch(where);
        OrderByBuilder orderBy = buildSortDir(where);
        orderBy = buildPostSort(orderBy);
        QueryBuilder limitBuilder = buildOffset(orderBy);

        return limitBuilder.build();
    }

    private QueryBuilder buildOffset(OrderByBuilder orderBy) {
        return orderBy.limit(limit, offset);
    }

    private OrderByBuilder buildPostSort(OrderByBuilder orderBy) {
        return orderBy.orderBy("id", SortDir.DESC);
    }

    private WhereBuilder buildPrecondition(WhereBuilder where) {
        return where.and(rel("enabled", EQ, true));
    }

    private WhereBuilder buildSearch(WhereBuilder where) {
        if (searchQuery != null) {
            List<String> keywords = Arrays.asList(searchQuery.split(" "));

            String cond = "CONCAT('%', ?, '%')";
            LikeCondition[] conds = keywords.stream()
                    .flatMap(s -> Stream.of(new LikeCondition("title", cond),
                            new LikeCondition("description", cond),
                            new LikeCondition("destination", cond)))
                    .collect(Collectors.toList())
                    .toArray(new LikeCondition[]{});

            return where.and(or(conds));
        }
        return where;
    }

    private OrderByBuilder buildSortDir(WhereBuilder where) {
        OrderByBuilder orderBy = where;

        if (hotFirst) {
            orderBy = orderBy.orderBy("hot", SortDir.DESC);
        }

        if (votesSort != null) {
            orderBy = orderBy.orderBy("votes_count", votesSort);
        }

        if (ratingSort != null) {
            orderBy = orderBy.orderBy("avg_rating", ratingSort);
        }

        if (priceSort != null) {
            orderBy = orderBy.orderBy("price", priceSort);
        }
        return orderBy;
    }

    private WhereBuilder buildTourTypes(WhereBuilder where) {
        if (tourTypes != null && !tourTypes.isEmpty()) {
            return where.and(in("type", false, tourTypes.stream()
                    .map(TourType::ordinal)
                    .map(String::valueOf)
                    .collect(Collectors.toList())
                    .toArray(new String[]{})));
        }

        return where;
    }

    private WhereBuilder buildPriceHigh(WhereBuilder where) {
        if (priceHigh != null) {
            return where.and(rel("price", LESSEQ, priceHigh));
        }
        return where;
    }

    private WhereBuilder buildPriceLow(WhereBuilder where) {
        if (priceLow != null) {
            return where.and(rel("price", GREATEREQ, priceLow));
        }
        return where;
    }

    public Boolean getHotFirst() {
        return hotFirst;
    }

    public Integer getPriceHigh() {
        return priceHigh;
    }

    public Integer getPriceLow() {
        return priceLow;
    }

    public SortDir getPriceSort() {
        return priceSort;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public EnumSet<TourType> getTourTypes() {
        return tourTypes;
    }

    public SortDir getRatingSort() {
        return ratingSort;
    }

    public SortDir getVotesSort() {
        return votesSort;
    }

    public boolean isHotFirst() {
        return hotFirst;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}

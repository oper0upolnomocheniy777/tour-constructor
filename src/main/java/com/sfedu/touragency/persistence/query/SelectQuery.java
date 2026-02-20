package com.sfedu.touragency.persistence.query;

import com.sfedu.touragency.persistence.query.condition.OrderByCondition;
import com.sfedu.touragency.util.Immutable;

import java.util.*;
import java.util.stream.*;

/**
 * Implementation of {@link Query} that encapsulates various parts
 * of a SQL selection query
 */
public class SelectQuery implements Query {
    private String[] columns;

    private String table;

    private BoolCondition whereClause;

    private List<OrderByCondition> orderByClause;

    private Integer limit;

    private Integer offset;

    private String cachedSql;

    public SelectQuery(String table, String... columns) {
        this.table = table;

        if(columns.length == 0) {
            this.columns = new String[]{"*"};
        } else {
            this.columns = columns;
        }
    }

    public SelectQuery(String table) {
        this.table = table;
        this.columns = new String[]{"*"};
    }

    public SelectQuery(SelectQuery query) {
        this.columns = query.columns;
        this.table = query.table;
        this.whereClause = query.whereClause;
        this.orderByClause = query.orderByClause;
        this.limit = query.limit;
    }

    @Override
    public String getSQL() {
        if(cachedSql != null) {
            return cachedSql;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(String.join(",", columns))
          .append(" FROM ").append(table);

        if(whereClause != null) {
            sb.append(" WHERE ");
            sb.append(whereClause.getSQL());
        }

        if(orderByClause != null) {
            List<String> orderByClausesStr = orderByClause.stream()
                      .map(OrderByCondition::getSQL)
                      .collect(Collectors.toList());
            sb.append(" ORDER BY ")
              .append(String.join(", ", orderByClausesStr));
        }

        if (limit != null) {
            if (offset != null) {
                sb.append(" LIMIT ").append(offset).append(", ").append(limit);
            } else {
                sb.append(" LIMIT ").append(limit);
            }
        }

        cachedSql = sb.toString();
        return cachedSql;
    }

    public SelectQuery setColumns(String... cols) {
        SelectQuery q = new SelectQuery(this);
        q.columns = cols;
        return q;
    }

    public SelectQuery setLimit(Integer limit) {
        SelectQuery q = new SelectQuery(this);
        q.limit = limit;
        return q;
    }

    public SelectQuery orderBy(List<OrderByCondition> orderBy) {
        SelectQuery q = new SelectQuery(this);
        q.orderByClause = Immutable.cons(orderByClause, orderBy);
        return q;
    }

    public SelectQuery orderBy(OrderByCondition... conditions) {
        return orderBy(Arrays.asList(conditions));
    }

    public SelectQuery setTable(String table) {
        SelectQuery q = new SelectQuery(this);
        q.table = table;
        return q;
    }

    public SelectQuery where(BoolCondition cond) {
        SelectQuery q = new SelectQuery(this);
        q.whereClause = cond;
        return q;
    }

    public SelectQuery setLimit(Integer limit, Integer offset) {
        SelectQuery q = new SelectQuery(this);
        q.limit = limit;
        q.offset = offset;
        return q;
    }

    public String[] getColumns() {
        return columns;
    }

    public Integer getLimit() {
        return limit;
    }

    public List<OrderByCondition> getOrderByClause() {
        return orderByClause;
    }

    public String getTable() {
        return table;
    }

    public BoolCondition getWhereClause() {
        return whereClause;
    }
}

package com.sfedu.touragency.persistence;

import org.apache.logging.log4j.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

/**
 * This class encapsulate a lot of boilerplate codes for JDBC
 * It also contains error handling and logging code
 */
public class JdbcTemplate {
    private static final Logger LOGGER = LogManager.getLogger(JdbcTemplate.class);

    private ConnectionManager connectionManager;

    public JdbcTemplate(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Starts a transaction by switching connection's auto commit status to false and
     *  and setting transaction isolation level
     *
     * Note, this method should be called only when the DataSource supports single thread
     * transaction spanning(e.g. DataSourceTxProxy)
     *
     * @param txIsolationLevel database transaction isolation level
     */
    public void startTransaction(int txIsolationLevel) {
        Connection txConnection = connectionManager.getConnection();

        try {
            txConnection.setAutoCommit(false);
            txConnection.setTransactionIsolation(txIsolationLevel);
        } catch (SQLException e) {
            LOGGER.error("Cannot start transaction. Your application may be data inconsistent", e);
            throw new RuntimeSqlException(e);
        } finally {
            tryClose(txConnection);
        }
    }

    /**
     * calls {@link #startTransaction(int)} with txIsolationLevel set to
     * Connection.TRANSACTION_READ_COMMITTED
     */
    public void startTransaction() {
        startTransaction(Connection.TRANSACTION_READ_COMMITTED);
    }

    /**
     * Commits the changes made by the underlying connection
     *
     * Note, this method should be called only when the DataSource supports single thread
     * transaction spanning(e.g. DataSourceTxProxy)
     */
    public void commit() {
        Connection conn = connectionManager.getConnection();
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.error("Cannot commit");
            throw new RuntimeSqlException(e);
        } finally {
            tryClose(conn);
        }
    }

    /**
     * Rollbacks the changes made by the underlying connection
     *
     * Note, this method should be called only when the DataSource supports single thread
     * transaction spanning(e.g. DataSourceTxProxy)
     */
    public void rollback() {
        Connection conn = connectionManager.getConnection();
        try {
            conn.rollback();
        } catch (SQLException e) {
            LOGGER.error("Cannot call rollback", e);
            throw new RuntimeSqlException(e);
        } finally {
            tryClose(conn);
        }
    }

    public void query(String query, ResultSetFunction fn, Object... params) {
        Connection conn = connectionManager.getConnection();

        if(conn == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();
            withRs(rs, fn);
        } catch (SQLException e) {
            LOGGER.warn("Error creating prepared statement. Query: " + query, e);
            throw new RuntimeSqlException(e);
        } finally {
            tryClose(conn);
        }
    }

    public <R> List<R> queryObjects(String query, EntityExtractor<R> producer, Object... params) {
        List<R> entities = new ArrayList<>();

        query(query, rs -> {
            while (rs.next()) {
                entities.add(producer.apply(rs));
            }
        }, params);

        return entities;
    }

    public <R> R queryObject(String query, EntityExtractor<R> producer, Object... params) {
        Object[] r = new Object[]{null};
        query(query, (rs) -> {
            if (rs.next()) {
                r[0] = producer.apply(rs);
            }
        }, params);

        return (R) r[0];
    }

    public int update(String updQuery, Object... params) {
        Connection conn = connectionManager.getConnection();

        if (conn == null)
            return 0;

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Cannot execute update query", e);
            throw new RuntimeSqlException(e);
        } finally {
            tryClose(conn);
        }
    }

    public Long insert(String updQuery, Object... params) {
        Connection conn = connectionManager.getConnection();

        if (conn == null)
            return null;

        try (PreparedStatement stmt
                     = conn.prepareStatement(updQuery, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()) {
                return rs.getLong(1);
            }

            return null;

        } catch (SQLException e) {
            LOGGER.error("Cannot insert values into DB", e);
            throw new RuntimeSqlException(e);
        } finally {
            tryClose(conn);
        }
    }

    public void withRs(ResultSet rs, ResultSetFunction fn) {
        try {
            fn.apply(rs);
        } catch (Exception e) {
            LOGGER.info("ResultSetFunctions has thrown an exception", e);
            throw new RuntimeSqlException(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.error("Cannot tryClose ResultSet", e);
                throw new RuntimeSqlException(e);
            }
        }
    }

    public boolean executeSqlFile(File file) {
        int line = 0;
        try {
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                List<String> queries = Arrays.asList(content.split(";"))
                        .stream()
                        .map(s -> s.trim())
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Connection conn = connectionManager.getConnection();

                try {
                    for (String query : queries) {
                        try (Statement stmt = conn.createStatement()) {
                            String tr = query.trim();
                            line++;
                            stmt.execute(query.trim());
                        }
                    }
                } finally {
                    tryClose(conn);
                }

                return true;
            }


        } catch (IOException e) {
            LOGGER.warn("SQL script file not found");
            return false;
        }catch (SQLException e) {
            LOGGER.trace("Errors while executing SQL script at #" + line +
                    " in file " + file.getAbsolutePath(), e);
            return false;
        }

        return false;
    }

    /**
     *
     * @param connection
     */
    private void tryClose(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Cannot close jdbc connection", e);
            throw new RuntimeSqlException(e);
        }
    }

    @FunctionalInterface
    public interface ResultSetFunction {
        void apply(ResultSet resultSet) throws SQLException;
    }

    @FunctionalInterface
    public interface EntityExtractor<R> {
        R apply(ResultSet resultSet) throws SQLException;
    }
}

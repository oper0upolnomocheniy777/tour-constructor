package com.sfedu.touragency.persistence.transaction;

import javax.sql.*;
import java.io.*;
import java.sql.*;
import java.util.logging.*;

/**
 * The data source enabled multiple method transaction span by intercepting connection
 * creation and release points
 *
 * <p>In a single thread a method that start a transaction and does not closes connection and
 * call other such methods will have a mutual transaction because all the methods will deal
 * with the same pooled connection instance </p>
 *
 * Use Transaction.tx for safe transaction creation
 *
 * Wraps a pooled DataSource.
 * {@see ConnectionTxProxy}
 * {@ses Transaction}
 */
public class DataSourceTxProxy implements DataSource {
    private DataSource ds;

    private final ThreadLocal<ConnectionTxProxy> connThreadLocal = new ThreadLocal<>();

    public DataSourceTxProxy(DataSource ds) {
        this.ds = ds;
    }

    void onFree() throws SQLException {
        ConnectionTxProxy conn = connThreadLocal.get();
        conn.getConnection().close();
        connThreadLocal.remove();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connThreadLocal.get() != null) {
            ConnectionTxProxy conn = connThreadLocal.get();
            conn.borrow();
            return conn;
        }

        ConnectionTxProxy conn = new ConnectionTxProxy(ds.getConnection(), this);
        conn.borrow();
        connThreadLocal.set(conn);
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return ds.getLoginTimeout();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return ds.getLogWriter();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return ds.getParentLogger();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        ds.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        ds.setLogWriter(out);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return ds.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return ds.unwrap(iface);
    }

    public void clean() {
        ConnectionTxProxy conn = connThreadLocal.get();

        if (conn != null) {
            try {
                conn.getConnection().close();
            } catch (SQLException e) {
                // LOGG
            } finally {
                connThreadLocal.remove();
            }
        }
    }
}

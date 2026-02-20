package com.sfedu.touragency.persistence.transaction;

import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.RuntimeSqlException;
import org.apache.logging.log4j.*;

import java.sql.*;

/**
 * Used to abstract a sequence of operations that should be transactional
 */
@FunctionalInterface
public interface Transaction {

    void span();

    /**
     * Wraps <i>transaction</i> into database transaction. If used with DataSourceTxProxy than
     * <i>transaction</i> could call other transactional methods
     * @param cm is the DataSource holder object
     * @param transaction is a method object or lambda that contains the database operations
     * @param transactionIsolationLevel is the level of database transaction isolation level
     * {@see DataSourceTxProxy}
     */
    static void tx(ConnectionManager cm, Transaction transaction, int transactionIsolationLevel) {
        Connection conn = cm.getConnection();

        boolean autoCommit;
        try {
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            if(conn.getTransactionIsolation() != transactionIsolationLevel) {
                conn.setTransactionIsolation(transactionIsolationLevel);
            }

            transaction.span();
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LogManager.getLogger().error("Cannot rollback transaction");
            }

            throw new RuntimeSqlException(e);
        } catch (Exception e) {
            LogManager.getLogger().error("Operations failed");
            throw e;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                LogManager.getLogger().error("Failed to close connection");
            }
        }
    }

    /**
     * @{link #tx(ConnectionManager, Transaction, int) called with read commited transaction
     * isolation level
     */
    static void tx(ConnectionManager cm, Transaction transaction) {
        tx(cm, transaction, Connection.TRANSACTION_READ_COMMITTED);
    }

}

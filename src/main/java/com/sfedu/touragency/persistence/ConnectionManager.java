package com.sfedu.touragency.persistence;

import com.mysql.jdbc.jdbc2.optional.*;
import com.sfedu.touragency.persistence.transaction.DataSourceTxProxy;
import com.sfedu.touragency.util.ResourcesUtil;
import org.apache.logging.log4j.*;

import javax.naming.*;
import javax.sql.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * ConnectionManager encapsulates a data source. The static factory methods
 * allow creating a DataSource object from various sources
 */
public class ConnectionManager {
    public static final Logger LOGGER = LogManager.getLogger(ConnectionManager.class);

    private static final java.lang.String JDBC_URL = "jdbc.url";
    private static final java.lang.String JDBC_USER = "jdbc.user";
    private static final java.lang.String JDBC_PASSWORD = "jdbc.password";
    private static final java.lang.String JDBC_POOL = "jdbc.pool";

    private DataSource dataSource;
    private int poolSize;


    public ConnectionManager(DataSource dataSource) {
        this.dataSource = dataSource;
        poolSize = 1;
    }


    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Cannot create connection to the database", e);
        }

        return null;
    }

    public void clean() {
        if (dataSource instanceof DataSourceTxProxy) {
            DataSourceTxProxy txDs = (DataSourceTxProxy) dataSource;
            txDs.clean();
        }
    }

    public int getPoolSize() {
        return poolSize;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Creates a ConnectionManager using MySQL JDBC connector driver
     * and loads its settings from a property file
     * @param filename
     * @return
     */
    public static ConnectionManager fromProperties(String filename) {
        MysqlDataSource mysqlDs = new MysqlDataSource();

        Properties props = loadProperties();

        mysqlDs.setUrl(props.getProperty(JDBC_URL));
        mysqlDs.setUser(props.getProperty(JDBC_USER, "root"));
        mysqlDs.setPassword(props.getProperty(JDBC_PASSWORD, "root"));

        ConnectionManager connManager = new ConnectionManager(mysqlDs);
        return connManager;
    }

    /**
     * Creates a ConnectionManager from a JNDI path of the DataSource location
     * in the component's environment
     * @param name - JNDI name of the DataSource component
     * @return a new ConnectionManager
     */
    public static ConnectionManager fromJndi(String name) {
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)envContext.lookup(name);
            DataSource txDs = new DataSourceTxProxy(ds);

            ConnectionManager connManager = new ConnectionManager(txDs);
            return connManager;
        } catch (NamingException e) {
            LOGGER.error("Cannot create InitialContext", e);
            return null;
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        File file = ResourcesUtil.getResourceFile("database.properties");

        try(InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.warn("Cannot load database.properties", e);
        }

        return properties;
    }

    public static ConnectionManager fromDs(DataSource ds) {
        return new ConnectionManager(new DataSourceTxProxy(ds));
    }
}

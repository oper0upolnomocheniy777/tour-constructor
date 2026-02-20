package com.sfedu.touragency.persistence;

import com.sfedu.touragency.util.ResourcesUtil;
import org.h2.jdbcx.*;

public class H2Db {
    public static ConnectionManager init(String initScript) {
        JdbcDataSource ds = getH2DS();

        ConnectionManager connectionManager = new ConnectionManager(ds);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connectionManager);
        jdbcTemplate.executeSqlFile(ResourcesUtil.getResourceFile(initScript));

        return connectionManager;
    }

    public static ConnectionManager initWithTx(String initScript) {
        JdbcDataSource ds = getH2DS();

        ConnectionManager connectionManager = ConnectionManager.fromDs(ds);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connectionManager);
        jdbcTemplate.executeSqlFile(ResourcesUtil.getResourceFile(initScript));

        return connectionManager;
    }

    private static JdbcDataSource getH2DS() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setUrl("jdbc:h2:mem:test;Mode=MYSQL;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("sa");
        return ds;
    }
}

package consumer_app.repository.db.db_managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import consumer_app.repository.db.db_connectors.Connector;
import consumer_app.repository.db.db_connectors.MySQLConnector;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Properties;

public class MySQLConnectorManager implements ConnectorManager {

    private final Logger logger = LoggerFactory.getLogger(MySQLConnectorManager.class);

    public static class Builder implements ManagerBuilder {

        private static final String CACHE_PREP_STMTS = "cachePrepStmts";
        private static final String PREP_STMTS_CACHE_SIZE = "prepStmtCacheSize";
        private static final String PREP_STMTS_CACHE_LIMIT = "prepStmtCacheSqlLimit";

        private HikariConfig config;

        public Builder() {
            config = new HikariConfig();
        }

        public Builder(@NotNull Properties properties) {
            config = new HikariConfig(properties);
        }

        public Builder setUrl(@NotNull String url) {
            config.setJdbcUrl(url);
            return this;
        }

        public Builder setUsername(@NotNull String username) {
            config.setUsername(username);
            return this;
        }

        public Builder setPassword(@NotNull String password) {
            config.setPassword(password);
            return this;
        }

        public Builder setMaximumPoolSize(int poolSize) {
            config.setMaximumPoolSize(poolSize);
            return this;
        }

        public Builder setAllowPoolSuspension(boolean allowPoolSuspension) {
            config.setAllowPoolSuspension(allowPoolSuspension);
            return this;
        }

        public Builder setLeakDetectionThreshold(int threshold) {
            config.setLeakDetectionThreshold(threshold);
            return this;
        }

        public Builder setAutoCommit(boolean autoCommit) {
            config.setAutoCommit(autoCommit);
            return this;
        }

        public Builder setDriverClassName(@NotNull String driverClassName) {
            config.setDriverClassName(driverClassName);
            return this;
        }

        public Builder setPoolName(String poolName) {
            config.setPoolName(poolName);
            return this;
        }

        public Builder setDriverClass(@NotNull Class<?> driverClass) {
            return setDriverClassName(driverClass.getName());
        }

        public Builder setConnectionTimeout(int timeout) {
            config.setConnectionTimeout(timeout);
            return this;
        }

        public Builder setMinimumIdle(int minimumIdle) {
            config.setMinimumIdle(minimumIdle);
            return this;
        }

        public Builder setConnectionTestQuery(String query) {
            if (query != null) {
                config.setConnectionTestQuery(query);
            }
            return this;
        }

        public Builder setPrepStmtsCacheSize(int cacheSize) {
            config.addDataSourceProperty(PREP_STMTS_CACHE_SIZE, cacheSize);
            return this;
        }

        public Builder setPrepStmtsCacheLimit(int cacheLimit) {
            config.addDataSourceProperty(PREP_STMTS_CACHE_LIMIT, cacheLimit);
            return this;
        }

        public Builder setCachePrepStmts(boolean cache) {
            config.addDataSourceProperty(CACHE_PREP_STMTS, cache);
            return this;
        }

        public Builder addSourceProperty(String propertyName, String propetryValue) {
            config.addDataSourceProperty(propertyName, propetryValue);
            return this;
        }

        @Override
        public ConnectorManager build() {
            return new MySQLConnectorManager(config);
        }
    }

    private HikariDataSource dataSource;

    private MySQLConnectorManager(HikariConfig config) {
        dataSource = new HikariDataSource(config);
    }

    @Override
    public Connector getConnector() throws SQLException {
        return new MySQLConnector(dataSource.getConnection());
    }
}

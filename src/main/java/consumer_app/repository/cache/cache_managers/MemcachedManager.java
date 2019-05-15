package consumer_app.repository.cache.cache_managers;

import consumer_app.repository.cache.cache_connectors.CacheConnector;
import consumer_app.repository.cache.cache_connectors.MemcachedConnector;

import java.io.IOException;

public class MemcachedManager implements CacheManager {

    public static class Builder implements ManagerBuilder {

        private String host = "localhost";
        private int port = 11211;
        private int expirationTime = 0;
        private int timeout = 3000;
        private MemcachedConnector.LogLevel logLevel;

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setExpirationTimeMillis(int expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public Builder setOperationTimeoutMillis(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setLogLevel(MemcachedConnector.LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public CacheManager build() {
            return new MemcachedManager(host, port, expirationTime, timeout, logLevel);
        }
    }

    private String host;
    private int port;
    private int expirationTime;
    private int timeout;
    private MemcachedConnector.LogLevel logLevel;

    private MemcachedManager (String host, int port, int expirationTime, int timeout, MemcachedConnector.LogLevel logLevel) {
        this.host = host;
        this.port = port;
        this.expirationTime = expirationTime;
        this.timeout = timeout;
        this.logLevel = logLevel;
    }

    @Override
    public CacheConnector getCacheConnector() throws IOException {
        return new MemcachedConnector(host, port, expirationTime, timeout, logLevel);
    }
}

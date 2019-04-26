package consumer_app.repository.cache.cache_managers;

import consumer_app.repository.cache.cache_connectors.CacheConnector;
import consumer_app.repository.cache.cache_connectors.MemcachedConnector;

import java.io.IOException;

public class MemcachedManager implements CacheManager {

    private String host;
    private int port;
    private int expirationTime;

    public MemcachedManager(String host, int port) {
        this(host, port, 0);
    }

    public MemcachedManager(String host, int port, int expirationTime) {
        this.host = host;
        this.port = port;
        this.expirationTime = expirationTime;
    }

    @Override
    public CacheConnector getCacheConnector() throws IOException {
        return new MemcachedConnector(host, port, expirationTime);
    }
}

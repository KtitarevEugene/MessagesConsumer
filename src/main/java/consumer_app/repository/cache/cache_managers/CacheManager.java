package consumer_app.repository.cache.cache_managers;

import consumer_app.repository.cache.cache_connectors.CacheConnector;

public interface CacheManager {
    CacheConnector getCacheConnector() throws Exception;
}

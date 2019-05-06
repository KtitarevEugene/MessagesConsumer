package consumer_app.repository.repository_types;

import consumer_app.repository.cache.cache_connectors.CacheConnector;
import consumer_app.repository.cache.cache_managers.CacheManager;
import consumer_app.repository.db.db_connectors.Connector;
import consumer_app.repository.db.db_managers.ConnectorManager;
import consumer_app.repository.exceptions.CacheConnectionException;
import consumer_app.repository.exceptions.NoDataInCacheException;
import consumer_app.repository.exceptions.NoDataInDBException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.repository.db.db_models.ResultModel;

import java.io.IOException;
import java.util.List;

public class CachedRepository implements Repository {

    private final Logger logger = LoggerFactory.getLogger(CachedRepository.class);

    private static final String VALUE_KEY = "VALUE_PREFIX_KEY_%s";

    private ConnectorManager connectorManager;
    private CacheManager cacheManager;

    @Contract(pure = true)
    public CachedRepository(ConnectorManager connectorManager, CacheManager cacheManager) {
        this.connectorManager = connectorManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean updateResultModel (ResultModel model) {
        try (Connector connector = connectorManager.getConnector()) {
            connector.updateResultModel(model);
            deleteModelInCache(model.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    @Nullable
    @Override
    public List<ResultModel> getResultByValue (String value) {
        List<ResultModel> resultModels = null;
        try {
            logger.info("Searching for result for value {} in cache...", value);
            return retrieveFromCache(value);
        } catch (NoDataInCacheException e) {
            logger.warn("Result in cache not found.");
            try {
                logger.info("Getting result from db...");
                resultModels = retrieveFromDatabase(value);

                if (!resultModels.isEmpty()) {
                    logger.info("Done. Saving result to cache.");
                    putModelsListToCache(value, resultModels);
                }
            } catch (NoDataInDBException ex ) {
                logger.warn("Result for value {} not found", value);
            } catch (CacheConnectionException ex) {
                logger.error("Error has happened during connection to cache.", ex);
            }
        } catch (CacheConnectionException e) {
            logger.warn("Error has happened during connection to cache.");
            try {
                logger.info("Getting data from db...");
                resultModels = retrieveFromDatabase(value);
            } catch (NoDataInDBException ex ) {
                logger.warn("Result for value {} not found", value);
            }
        }

        return resultModels;
    }

    @Nullable
    @Override
    public ResultModel getResultById(int id) {
        ResultModel resultModel = null;
        try {
            logger.info("Getting for result by id = {} in cache...", id);
            return retrieveFromCache(id);
        } catch (NoDataInCacheException e) {
            logger.warn("Result in cache not found.");
            try {
                logger.info("Getting result from db...");
                resultModel = retrieveFromDatabase(id);

                logger.info("Done. Saving result to cache.");
                putModelToCache(id, resultModel);
            } catch (NoDataInDBException ex) {
                logger.warn("Result with id = {} not found", id);
            } catch ( CacheConnectionException ex ) {
                logger.error("Error has happened during connection to cache.", ex);
            }
        } catch (CacheConnectionException e) {
            logger.warn("Error has happened during connection to cache.");
            try {
                logger.info("Getting data from db...");
                resultModel = retrieveFromDatabase(id);
            } catch (NoDataInDBException ex ) {
                logger.warn("Result with id = {} not found", id);
            }
        }

        return resultModel;
    }

    @NotNull
    private ResultModel retrieveFromDatabase(int id) throws NoDataInDBException {
        try (Connector connector = connectorManager.getConnector()) {
            ResultModel model = connector.getResultById(id);
            if (model != null) {
                return model;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        throw new NoDataInDBException();
    }

    @NotNull
    private List<ResultModel> retrieveFromDatabase(String value) throws NoDataInDBException {
        try (Connector connector = connectorManager.getConnector()) {
            List<ResultModel> resultModels = connector.getResultByValue(value);
            if (resultModels != null) {
                return resultModels;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        throw new NoDataInDBException();
    }

    @NotNull
    private List<ResultModel> retrieveFromCache(String value) throws CacheConnectionException, NoDataInCacheException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            List<ResultModel> resultModels = cacheConnector.getResultModelList(String.format(VALUE_KEY, value));
            if (resultModels != null) {
                return resultModels;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            if (e instanceof IOException) {
                throw new CacheConnectionException();
            }
        }

        throw new NoDataInCacheException();
    }

    @NotNull
    private ResultModel retrieveFromCache(int id) throws NoDataInCacheException, CacheConnectionException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            ResultModel model = cacheConnector.getResultModel(id);
            if (model != null) {
                return model;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            if (e instanceof IOException) {
                throw new CacheConnectionException();
            }
        }

        throw new NoDataInCacheException();
    }

    private void putModelToCache(int id, ResultModel model) throws CacheConnectionException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            cacheConnector.addResultModel(id, model);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new CacheConnectionException();
        }
    }

    private void putModelsListToCache(String key, List<ResultModel> models) throws CacheConnectionException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {

            cacheConnector.addResultModelList(String.format(VALUE_KEY, key), models);

            for (ResultModel model : models) {
                cacheConnector.addResultModel(model.getId(), model);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new CacheConnectionException();
        }
    }

    private void deleteModelInCache(int id) {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            cacheConnector.deleteResultModel(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}

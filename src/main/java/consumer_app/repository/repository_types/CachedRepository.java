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
import web_app.repository.db.db_models.ResultModel;

import java.io.IOException;
import java.util.List;

public class CachedRepository implements Repository {

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
            e.printStackTrace();
        }

        return false;
    }

    @Nullable
    @Override
    public List<ResultModel> getResultByValue (String value) {
        List<ResultModel> resultModels = null;
        try {
            return retrieveFromCache(value);
        } catch (NoDataInCacheException e) {
            try {
                resultModels = retrieveFromDatabase(value);
                putModelsListToCache(value, resultModels);
            } catch (NoDataInDBException | CacheConnectionException ex ) {
                ex.printStackTrace();
            }
        } catch (CacheConnectionException e) {
            e.printStackTrace();
            try {
                resultModels = retrieveFromDatabase(value);
            } catch (NoDataInDBException ex ) {
                ex.printStackTrace();
            }
        }

        return resultModels;
    }

    @Nullable
    @Override
    public ResultModel getResultById(int id) {
        ResultModel resultModel = null;
        try {
            return retrieveFromCache(id);
        } catch (NoDataInCacheException e) {
            try {
                resultModel = retrieveFromDatabase(id);
                putModelToCache(id, resultModel);
            } catch (NoDataInDBException | CacheConnectionException ex ) {
                ex.printStackTrace();
            }
        } catch (CacheConnectionException e) {
            e.printStackTrace();
            try {
                resultModel = retrieveFromDatabase(id);
            } catch (NoDataInDBException ex ) {
                ex.printStackTrace();
            }
        }

        return resultModel;
    }

    @NotNull
    private ResultModel retrieveFromDatabase(int id) throws NoDataInDBException {
        try (Connector connector = connectorManager.getConnector()) {
            return connector.getResultById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new NoDataInDBException();
    }

    @NotNull
    private List<ResultModel> retrieveFromDatabase(String value) throws NoDataInDBException {
        try (Connector connector = connectorManager.getConnector()) {
            return connector.getResultByValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new NoDataInDBException();
    }

    @NotNull
    private List<ResultModel> retrieveFromCache(String value) throws CacheConnectionException, NoDataInCacheException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            return cacheConnector.getResultModelList(String.format(VALUE_KEY, value));
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                throw new CacheConnectionException();
            }
        }

        throw new NoDataInCacheException();
    }

    @NotNull
    private ResultModel retrieveFromCache(int id) throws NoDataInCacheException, CacheConnectionException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            return cacheConnector.getResultModel(id);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            throw new CacheConnectionException();
        }
    }

    private void deleteModelInCache(int id) {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            cacheConnector.deleteResultModel(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

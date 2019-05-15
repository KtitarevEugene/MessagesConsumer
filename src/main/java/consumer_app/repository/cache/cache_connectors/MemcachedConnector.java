package consumer_app.repository.cache.cache_connectors;

import org.jetbrains.annotations.NotNull;
import web_app.repository.db.db_models.ResultModel;
import net.spy.memcached.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class MemcachedConnector implements CacheConnector {

    static {
        System.setProperty("net.spy.log.LoggerImpl",
                "net.spy.memcached.compat.log.SunLogger");
    }

    public static final String CACHE_LOGGER_NAME = "net.spy.memcached";

    public enum LogLevel {
        OFF("OFF"),
        SEVERE("SEVERE"),
        WARNING("WARNING"),
        INFO("INFO"),
        CONFIG("CONFIG"),
        FINE("FINE"),
        FINER("FINER"),
        FINEST("FINEST"),
        ALL("ALL");

        private String level;

        LogLevel(String level) {
            this.level = level;
        }

        @Override
        public String toString() {
            return level;
        }
    }

    private MemcachedClient cacheClient;
    private int expirationTime;

    public MemcachedConnector(String host, int port, int expirationTime, int timeout, LogLevel logLevel) throws IOException {

        this.expirationTime = expirationTime;

        ConnectionFactory connectionFactory = new ConnectionFactoryBuilder()
                .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                .setFailureMode(FailureMode.Redistribute)
                .setOpTimeout(timeout)
                .build();

        cacheClient = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(String.format("%s:%d", host, port)));
        setLogLevel(logLevel);
    }

    private void setLogLevel(@NotNull LogLevel logLevel) {
        java.util.logging.Logger.getLogger(CACHE_LOGGER_NAME)
                .setLevel(Level.parse(logLevel.toString()));
    }

    @Override
    public boolean addResultModel(int id, ResultModel model) throws ExecutionException, InterruptedException {
        return cacheClient.add(String.valueOf(id), expirationTime, model).get();
    }

    @Override
    public boolean setResultModel(int id, ResultModel model) throws ExecutionException, InterruptedException {
        ResultModel existModel = getResultModel(id);
        if (existModel != null) {
            return cacheClient.set(String.valueOf(id), expirationTime, model).get();
        } else {
            return addResultModel(id, model);
        }
    }

    @Override
    public ResultModel getResultModel(int id) {
        Object obj = cacheClient.get(String.valueOf(id));
        if (obj instanceof ResultModel) {
            return (ResultModel) obj;
        }

        return null;
    }

    @Override
    public boolean deleteResultModel(int id) throws ExecutionException, InterruptedException {
        return cacheClient.delete(String.valueOf(id)).get();
    }

    @Override
    public boolean addResultModelList(String key, List<ResultModel> models) throws ExecutionException, InterruptedException {
        return cacheClient.add(key, expirationTime, models).get();
    }

    @Override
    public List<ResultModel> getResultModelList(String key) {
        Object obj = cacheClient.get(key);
        if (obj instanceof List) {
            return (List<ResultModel>) obj;
        }

        return null;
    }

    @Override
    public void close() {
        cacheClient.shutdown();
    }
}

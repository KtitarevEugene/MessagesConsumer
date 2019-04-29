package consumer_app.repository.cache.cache_connectors;

import web_app.repository.db.db_models.ResultModel;
import net.spy.memcached.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemcachedConnector implements CacheConnector {

    private MemcachedClient cacheClient;
    private int expirationTime;

    public MemcachedConnector(String host, int port, int expirationTime, int timeout) throws IOException {

        this.expirationTime = expirationTime;

        ConnectionFactory connectionFactory = new ConnectionFactoryBuilder()
                .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                .setFailureMode(FailureMode.Redistribute)
                .setOpTimeout(timeout)
                .build();

        cacheClient = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(String.format("%s:%d", host, port)));
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

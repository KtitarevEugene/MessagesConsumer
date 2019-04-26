package consumer_app.repository.cache.cache_connectors;

import web_app.repository.db.db_models.ResultModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CacheConnector extends AutoCloseable {
    boolean addResultModel(int id, ResultModel model) throws ExecutionException, InterruptedException;
    boolean setResultModel(int id, ResultModel model) throws ExecutionException, InterruptedException;
    ResultModel getResultModel(int id);
    boolean deleteResultModel(int id) throws ExecutionException, InterruptedException;

    boolean addResultModelList(String key, List<ResultModel> models) throws ExecutionException, InterruptedException;
    List<ResultModel> getResultModelList(String key);
}

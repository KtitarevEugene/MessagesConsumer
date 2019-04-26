package consumer_app.repository.db.db_connectors;

import web_app.repository.db.db_models.ResultModel;

import java.util.List;

public interface Connector extends AutoCloseable {
    ResultModel getResultById(int id);
    List<ResultModel> getResultByValue (String value);
    boolean updateResultModel (ResultModel model);
}

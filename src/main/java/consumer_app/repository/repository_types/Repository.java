package consumer_app.repository.repository_types;

import web_app.repository.db.db_models.ResultModel;

import java.util.List;

public interface Repository {
    boolean updateResultModel (ResultModel model);
    List<ResultModel> getResultByValue (String value);
    ResultModel getResultById(int id);
}

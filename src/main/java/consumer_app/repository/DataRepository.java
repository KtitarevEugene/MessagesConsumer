package consumer_app.repository;

import consumer_app.repository.repository_types.Repository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import web_app.repository.db.db_models.ResultModel;

import java.util.List;

public class DataRepository implements Repository {

    private Repository repositoryType;

    @Contract(pure = true)
    public DataRepository() {
        this(null);
    }

    @Contract(pure = true)
    public DataRepository(Repository repositoryType) {
        this.repositoryType = repositoryType;
    }

    public void setRepositoryType(Repository repositoryType) {
        this.repositoryType = repositoryType;
    }

    @Override
    public boolean updateResultModel (ResultModel model) {
        return repositoryType.updateResultModel(model);
    }

    @Nullable
    @Override
    public List<ResultModel> getResultByValue (String value) {
        return repositoryType.getResultByValue(value);
    }

    @Nullable
    @Override
    public ResultModel getResultById(int id) {
        return repositoryType.getResultById(id);
    }
}

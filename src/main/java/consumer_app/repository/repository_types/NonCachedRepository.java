package consumer_app.repository.repository_types;

import consumer_app.repository.db.db_connectors.Connector;
import consumer_app.repository.db.db_managers.ConnectorManager;
import consumer_app.repository.exceptions.NoDataInDBException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.repository.db.db_models.ResultModel;

import java.util.List;

public class NonCachedRepository implements Repository {

    private Logger logger = LoggerFactory.getLogger(NonCachedRepository.class);

    private ConnectorManager connectorManager;

    public NonCachedRepository(ConnectorManager connectorManager) {
        this.connectorManager = connectorManager;
    }

    @Override
    public boolean updateResultModel (ResultModel model) {
        try (Connector connector = connectorManager.establishConnection()) {
            logger.info("Updating result in db...");
            return connector.updateResultModel(model);
        } catch (Exception e) {
            logger.warn("Error has happened during attempt to update result.", e);
        }

        return false;
    }

    @Override
    public List<ResultModel> getResultByValue (String value) {
        List<ResultModel> resultModels = null;
        try {
            logger.info("Getting result from db...");
            resultModels = retrieveFromDatabase(value);
        } catch (NoDataInDBException ex) {
            logger.warn("Result for value {} not found", value);
        }

        return resultModels;
    }

    @Override
    public ResultModel getResultById(int id) {
        ResultModel resultModel = null;
        try {
            logger.info("Getting result with id = {} from db...", id);
            resultModel = retrieveFromDatabase(id);
        } catch (NoDataInDBException ex) {
            logger.warn("Result with id = {} not found", id);
        }

        return resultModel;
    }

    @NotNull
    private ResultModel retrieveFromDatabase(int id) throws NoDataInDBException {
        try (Connector connector = connectorManager.establishConnection()) {
            ResultModel resultModel = connector.getResultById(id);
            if (resultModel != null) {
                return resultModel;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        throw new NoDataInDBException();
    }

    @NotNull
    private List<ResultModel> retrieveFromDatabase(String value) throws NoDataInDBException {
        try (Connector connector = connectorManager.establishConnection()) {
            List<ResultModel> resultModels = connector.getResultByValue(value);
            if (resultModels != null) {
                return resultModels;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        throw new NoDataInDBException();
    }
}

package consumer_app.repository.repository_types;

import consumer_app.repository.db.db_connectors.Connector;
import consumer_app.repository.db.db_managers.ConnectorManager;
import consumer_app.repository.exceptions.NoDataInDBException;
import web_app.repository.db.db_models.ResultModel;

import java.util.List;

public class NonCachedRepository implements Repository {

    private ConnectorManager connectorManager;

    public NonCachedRepository(ConnectorManager connectorManager) {
        this.connectorManager = connectorManager;
    }

    @Override
    public boolean updateResultModel (ResultModel model) {
        try (Connector connector = connectorManager.getConnector()) {
            connector.updateResultModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<ResultModel> getResultByValue (String value) {
        List<ResultModel> resultModels = null;
        try {
            resultModels = retrieveFromDatabase(value);
        } catch (NoDataInDBException ex) {
            ex.printStackTrace();
        }

        return resultModels;
    }

    @Override
    public ResultModel getResultById(int id) {
        ResultModel resultModel = null;
        try {
            resultModel = retrieveFromDatabase(id);
        } catch (NoDataInDBException ex) {
            ex.printStackTrace();
        }

        return resultModel;
    }

    private ResultModel retrieveFromDatabase(int id) throws NoDataInDBException {
        try (Connector connector = connectorManager.getConnector()) {
            return connector.getResultById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new NoDataInDBException();
    }

    private List<ResultModel> retrieveFromDatabase(String value) throws NoDataInDBException {
        try (Connector connector = connectorManager.getConnector()) {
            return connector.getResultByValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new NoDataInDBException();
    }
}

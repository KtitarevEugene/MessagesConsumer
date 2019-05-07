package consumer_app.repository.db.db_connectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.repository.db.db_models.ResultModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLConnector implements Connector {

    private final Logger logger = LoggerFactory.getLogger(MySQLConnector.class);

    private Connection connection;

    public MySQLConnector(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<ResultModel> getResultByValue (String value) {
        try (PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `value` = ? ;")) {
            statement.setString(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {

                return fetchModelsList(resultSet);
            }

        } catch (SQLException e) {
            logger.error("SQL exception has been thrown.", e);
        }

        return null;
    }

    @Override
    public boolean updateResultModel (ResultModel model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE `results`.`prime_numbers` SET `value` = ?, `results` = ? WHERE `id` = ?;")) {

            statement.setString(1, model.getValue());
            statement.setString(2, new Gson().toJson(model.getPrimeNumbers(), new TypeToken<List<Integer>>(){}.getType()));
            statement.setInt(3, model.getId());

            return statement.execute();
        } catch (SQLException e) {
            logger.error("SQL exception has been thrown.", e);
        }

        return false;
    }

    @Override
    public ResultModel getResultById(int id) {
        try (PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `id` = ? ;")) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                List<ResultModel> models = fetchModelsList(resultSet);
                return !models.isEmpty() ? models.get(0) : null;
            }

        } catch (SQLException e) {
            logger.error("SQL exception has been thrown.", e);
        }

        return null;
    }

    private PreparedStatement getQueryStatement(String query) throws SQLException {
        return connection.prepareCall(query);
    }

    private List<ResultModel> fetchModelsList (ResultSet resultSet) throws SQLException {
        List<ResultModel> models = new ArrayList<>();
        while (resultSet.next()) {
            ResultModel model = fetchModel(resultSet);
            models.add(model);
        }

        return models;
    }

    private ResultModel fetchModel (ResultSet resultSet) throws SQLException {
        ResultModel model = new ResultModel();

        model.setId(resultSet.getInt(1));
        model.setValue(resultSet.getString(2));
        model.setPrimeNumbers(new Gson().fromJson(resultSet.getString(3), new TypeToken<List<Integer>>(){}.getType()));
        model.setCreateTime(new Date(resultSet.getTimestamp(4).getTime()));

        return model;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}

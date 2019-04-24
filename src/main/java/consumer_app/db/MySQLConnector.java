package consumer_app.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import consumer_app.db.models.ResultModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLConnector implements AutoCloseable {

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/results?useSSL=false";
    private Connection connection;

    public MySQLConnector(String userName, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(CONNECTION_URL, userName, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ResultModel> getResultByValue (String value) {
        try (PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `value` = ? ;")) {
            statement.setString(1, value);

            ResultSet resultSet = statement.executeQuery();

            return fetchModelsList(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateResultModel (ResultModel model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE `results`.`prime_numbers` SET `value` = ?, `results` = ? WHERE `id` = ?;")) {

            statement.setString(1, model.getValue());
            statement.setString(2, new Gson().toJson(model.getPrimeNumbers(), new TypeToken<List<Integer>>(){}.getType()));
            statement.setInt(3, model.getId());

            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ResultModel getResultById(int id) {
        try (PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `id` = ? ;")) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            return fetchModelsList(resultSet).get(0);

        } catch (SQLException e) {
            e.printStackTrace();
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

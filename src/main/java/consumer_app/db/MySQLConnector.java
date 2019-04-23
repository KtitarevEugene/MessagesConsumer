package consumer_app.db;

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

    public boolean insertResultModel(ResultModel model) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `results`.`prime_numbers` (`value`, `results`, `queue_id`, `create_time`) VALUES (?, ?, ?, ?);");

            statement.setString(1, model.getValue());
            statement.setString(2, model.getPrimeNumbers());
            statement.setString(3, model.getQueueId());
            statement.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));

            return statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<ResultModel> getResultByValue (String value) {
        try {
            PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `value` = ? ;");
            statement.setString(1, value);

            ResultSet resultSet = statement.executeQuery();

            return fetchModelsList(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultModel getResultById(int id) {
        try {
            PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `id` = ? ;");
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
        model.setPrimeNumbers(resultSet.getString(3));
        model.setQueueId(resultSet.getString(4));
        model.setCreateTime(new Date(resultSet.getTimestamp(5).getTime()));

        return model;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}

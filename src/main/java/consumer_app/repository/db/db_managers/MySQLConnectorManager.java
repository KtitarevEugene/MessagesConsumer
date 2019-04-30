package consumer_app.repository.db.db_managers;

import consumer_app.common.Constants;
import consumer_app.repository.db.db_connectors.Connector;
import consumer_app.repository.db.db_connectors.MySQLConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MySQLConnectorManager implements ConnectorManager {

    private final Logger logger = LoggerFactory.getLogger(MySQLConnectorManager.class);

    private String url = "jdbc:mysql://localhost:3306/results";
    private String userName = "root";
    private String password = "root";

    public MySQLConnectorManager(Properties properties) {

        String connectionStr = getProperty(properties, Constants.JDBC_URL);
        if (connectionStr != null) {
            url = connectionStr;
        }

        String user = getProperty(properties, Constants.JDBC_USER);
        if (user != null) {
            userName = user;
        }

        String pass = getProperty(properties, Constants.JDBC_PASSWORD);
        if (pass != null) {
            password = pass;
        }
    }

    public MySQLConnectorManager(String url, String userName, String password) {

        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    private String getProperty(Properties properties, String name) {
        String value = properties.getProperty(name);
        if (value == null) {
            logger.warn("Missing config property '{}'. Used default value.", name);
        }

        return value;
    }

    @Override
    public Connector getConnector() {
        return new MySQLConnector(url, userName, password);
    }
}

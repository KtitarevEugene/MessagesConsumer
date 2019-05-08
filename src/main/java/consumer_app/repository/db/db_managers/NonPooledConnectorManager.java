package consumer_app.repository.db.db_managers;

import consumer_app.repository.db.db_connectors.Connector;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class NonPooledConnectorManager implements ConnectorManager{

    public static class Builder implements ManagerBuilder {

        private Connector connector;
        private String url;
        private Properties properties;

        public Builder(Connector connector) {
            this.connector = connector;
            properties = new Properties();
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setUsername(String username) {
            properties.setProperty("user", username);
            return this;
        }

        public Builder setPassword(String password) {
            properties.setProperty("password", password);
            return this;
        }

        public Builder setCustomParameter(String paramName, String paramValue) {
            properties.setProperty(paramName, paramValue);
            return this;
        }

        @Override
        public ConnectorManager build() {
            return new NonPooledConnectorManager(connector, url, properties);
        }
    }

    private Connector connector;
    private String url;
    private Properties properties;

    private NonPooledConnectorManager(Connector connector, String url, Properties properties) {
        this.connector = connector;
        this.url = url;
        this.properties = properties;
    }

    @Override
    public Connector establishConnection() throws SQLException {
        connector.setConnection(DriverManager.getConnection(url, properties));
        return connector;
    }
}

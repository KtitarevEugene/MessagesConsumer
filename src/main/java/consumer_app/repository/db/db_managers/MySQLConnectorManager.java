package consumer_app.repository.db.db_managers;

import consumer_app.repository.db.db_connectors.Connector;
import consumer_app.repository.db.db_connectors.MySQLConnector;

public class MySQLConnectorManager implements ConnectorManager {

    private String userName;
    private String password;

    public MySQLConnectorManager(String userName, String password) {

        this.userName = userName;
        this.password = password;
    }

    @Override
    public Connector getConnector() {
        return new MySQLConnector(userName, password);
    }
}

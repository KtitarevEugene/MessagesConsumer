package consumer_app.repository.db.db_managers;

import consumer_app.repository.db.db_connectors.Connector;

import java.sql.SQLException;

public interface ConnectorManager {
    Connector getConnector() throws SQLException;
}

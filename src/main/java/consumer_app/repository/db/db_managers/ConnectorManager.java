package consumer_app.repository.db.db_managers;

import consumer_app.repository.db.db_connectors.Connector;

public interface ConnectorManager {
    Connector getConnector();
}

package consumer_app.consumer;

import com.mysql.jdbc.Driver;
import consumer_app.common.Constants;
import consumer_app.repository.DataRepository;
import consumer_app.repository.cache.cache_managers.CacheManager;
import consumer_app.repository.cache.cache_managers.MemcachedManager;
import consumer_app.repository.db.db_connectors.MySQLConnector;
import consumer_app.repository.db.db_managers.ConnectorManager;
import consumer_app.repository.db.db_managers.NonPooledConnectorManager;
import consumer_app.repository.db.db_managers.PooledConnectorManager;
import consumer_app.prime_numbers.PrimesSearchFactory;
import consumer_app.prime_numbers.strategies_context.PrimesSearch;
import consumer_app.repository.repository_types.CachedRepository;
import consumer_app.repository.repository_types.NonCachedRepository;
import consumer_app.repository.repository_types.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.repository.db.db_models.ResultModel;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class ValuesMessagesListener implements Consumer.MessageListener {

    private final Logger logger = LoggerFactory.getLogger(ValuesMessagesListener.class);

    private DataRepository dataRepository;

    public ValuesMessagesListener(@NotNull Properties properties) throws SQLException {

        Repository repositoryType = getRepositoryType(properties);

        dataRepository = new DataRepository(repositoryType);
    }

    @NotNull
    private Repository getRepositoryType(@NotNull Properties properties) throws SQLException {

        String useCache = properties.getProperty(Constants.CACHE_USE_CACHE);

        if (useCache != null && useCache.equalsIgnoreCase(Constants.USE_CACHE_VALUE)) {
            CacheManager memcachedManager = new MemcachedManager.Builder()
                    .setHost(properties.getProperty(Constants.CACHE_HOST))
                    .setPort(Integer.parseInt(properties.getProperty(Constants.CACHE_PORT)))
                    .setOperationTimeoutMillis(Integer.parseInt(properties.getProperty(Constants.CACHE_TIMEOUT)))
                    .setExpirationTimeMillis(Integer.parseInt(properties.getProperty(Constants.CACHE_EXPIRATION_TIME)))
                    .build();

            return new CachedRepository(
                    getConnectorManager(properties),
                    memcachedManager);
        }

        return new NonCachedRepository(
                getConnectorManager(properties));

    }

    @NotNull
    private ConnectorManager getConnectorManager(@NotNull Properties properties) throws SQLException {
        DriverManager.registerDriver(new Driver());

        String usePool = properties.getProperty(Constants.JDBC_USE_CONNECTION_POOL);

        if (usePool.equalsIgnoreCase(Constants.USE_POOL_VALUE)) {

            PooledConnectorManager.Builder builder = new PooledConnectorManager.Builder(new MySQLConnector())
                    .setUrl(properties.getProperty(Constants.JDBC_URL))
                    .setUsername(properties.getProperty(Constants.JDBC_USER))
                    .setPassword(properties.getProperty(Constants.JDBC_PASSWORD))
                    .setConnectionTestQuery(properties.getProperty(Constants.JDBC_CONNECTION_TEXT_QUERY))
                    .setPoolName(properties.getProperty(Constants.JDBC_POOL_NAME));
            try {
                builder.setLeakDetectionThreshold(Integer.parseInt(properties.getProperty(Constants.JDBC_LEAK_DETECTION_THRESHOLD)));
            } catch (NumberFormatException ex) {
                logger.error("can't set {} parameter, used default value", Constants.JDBC_LEAK_DETECTION_THRESHOLD);
            }

            try {
                builder.setMaximumPoolSize(Integer.parseInt(properties.getProperty(Constants.JDBC_MAXIMUM_POOL_SIZE)));
            } catch (NumberFormatException ex) {
                logger.error("can't set {} parameter, used default value", Constants.JDBC_MAXIMUM_POOL_SIZE);
            }

            try {
                builder.setMinimumIdle(Integer.parseInt(properties.getProperty(Constants.JDBC_MINIMUM_IDLE)));
            } catch (NumberFormatException ex) {
                logger.error("can't set {} parameter, used default value", Constants.JDBC_LEAK_DETECTION_THRESHOLD);
            }

            for (Object obj : properties.keySet()) {
                if (obj instanceof String) {
                    String key = (String) obj;
                    if (key.startsWith(Constants.JDBC_PARAM_PREFIX)) {
                        builder.addSourceProperty(key.replace(Constants.JDBC_PARAM_PREFIX, ""), properties.getProperty(key));
                    }
                }
            }

            return builder.build();
        }


        NonPooledConnectorManager.Builder builder = new NonPooledConnectorManager.Builder(new MySQLConnector())
                .setUrl(properties.getProperty(Constants.JDBC_URL))
                .setUsername(properties.getProperty(Constants.JDBC_USER))
                .setPassword(properties.getProperty(Constants.JDBC_PASSWORD));

        for (Object obj : properties.keySet()) {
            if (obj instanceof String) {
                String key = (String) obj;
                if (key.startsWith(Constants.JDBC_PARAM_PREFIX)) {
                    builder.setCustomParameter(key.replace(Constants.JDBC_PARAM_PREFIX, ""), properties.getProperty(key));
                }
            }
        }

        return builder.build();
    }

    @Override
    public void onMessageReceived(Message message) {
        processReceivedMessage(message);
    }

    private void processReceivedMessage(@NotNull Message message) {
        Integer value = getMessageValue(message);
        if (value != null) {

            ResultModel model = getResultModelById(value);
            if (model != null) {

                List<Integer> primeNumbers = getPrimeNumbers(Integer.parseInt(model.getValue()));
                model.setPrimeNumbers(primeNumbers);

                addResultToRequestedValue(model);
            }
        }
    }

    @Nullable
    private Integer getMessageValue(@NotNull Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String value = textMessage.getText();

                return Integer.parseInt(value);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private ResultModel getResultModelById(int id) {
        return dataRepository.getResultById(id);
    }

    @NotNull
    private List<Integer> getPrimeNumbers(int value) {
        PrimesSearch primesSearch = PrimesSearchFactory.simpleSearchAlgorithm();
        return primesSearch.getPrimeNumbers(value);
    }

    private void addResultToRequestedValue(ResultModel resultModel) {
        dataRepository.updateResultModel(resultModel);
    }
}

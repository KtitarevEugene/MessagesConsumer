package consumer_app.main;

import consumer_app.common.Constants;
import consumer_app.common.Utils;
import consumer_app.consumer.Consumer;
import consumer_app.consumer.ValuesMessagesListener;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.io.*;
import java.util.Properties;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String[] jdbcParams = new String[] {
            Constants.JDBC_URL,
            Constants.JDBC_USER,
            Constants.JDBC_PASSWORD
    };
    private static final String[] activeMqParams = new String[] {
            Constants.ACTIVE_MQ_BROKER_URL,
            Constants.ACTIVE_MQ_CONSUMER_ID,
            Constants.ACTIVE_MQ_QUEUE_NAME
    };
    private static final String[] cacheParams = new String[] {
            Constants.CACHE_USE_CACHE,
            Constants.CACHE_HOST,
            Constants.CACHE_PORT,
            Constants.CACHE_TIMEOUT,
            Constants.CACHE_EXPIRATION_TIME
    };

    public static void main(String[] args) throws JMSException {
        try {
            Class.forName("web_app.repository.db.db_models.ResultModel");

            Properties config = getConfigProperties();

            Consumer consumer = new Consumer(config);

            consumer.createConnection();
            consumer.setMessageListener(new ValuesMessagesListener(config));
        } catch (ClassNotFoundException e) {
            logger.error("Class 'web_app.repository.db.db_models.ResultModel' not found.");
        } catch (IOException e) {
            logger.error("Config file {} not found.", System.getenv(Constants.ENV_VAR_NAME));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @NotNull
    private static Properties getConfigProperties() throws IOException {
        String filename = System.getenv(Constants.ENV_VAR_NAME);
        Reader configFileReader = new FileReader(filename);

        Properties properties = new Properties();

        Ini configFile = new Ini();
        configFile.load(configFileReader);

        Utils.addConfigParams(configFile, Constants.JDBC_CFG, jdbcParams, properties);
        Utils.addConfigParams(configFile, Constants.ACTIVE_MQ_CFG, activeMqParams, properties);
        Utils.addConfigParams(configFile, Constants.CACHE_CFG, cacheParams, properties);

        return properties;
    }
}

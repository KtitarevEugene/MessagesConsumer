package consumer_app.main;

import com.sun.istack.NotNull;
import consumer_app.common.Constants;
import consumer_app.consumer.Consumer;
import consumer_app.consumer.ValuesMessagesListener;
import org.ini4j.Ini;
import org.ini4j.Profile;

import javax.jms.JMSException;
import java.io.*;
import java.util.Properties;

public class Main {

    private static final String[] jdbcParams = new String[] {
            Constants.JDBC_URL,
            Constants.JDBC_USER,
            Constants.JDBC_PASSWORD
    };
    private static final String[] activeMqParams = new String[] {
            Constants.ACTIVE_MQ_BROKER_URL,
            Constants.ACTIVE_MQ_CLIENT_ID,
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Properties getConfigProperties() throws IOException {
        String filename = System.getenv(Constants.ENV_VAR_NAME);
        Reader configFileReader = new FileReader(filename);

        Properties properties = new Properties();

        Ini configFile = new Ini();
        configFile.load(configFileReader);

        addConfigParams(configFile, Constants.JDBC_CFG, jdbcParams, properties);
        addConfigParams(configFile, Constants.ACTIVE_MQ_CFG, activeMqParams, properties);
        addConfigParams(configFile, Constants.CACHE_CFG, cacheParams, properties);

        return properties;
    }

    private static void addConfigParams(Ini config, String sectionName, String[] params, Properties properties) {
        Profile.Section section = config.get(sectionName);
        for (String param : params) {
            properties.setProperty(param, section.get(param));
        }
    }
}

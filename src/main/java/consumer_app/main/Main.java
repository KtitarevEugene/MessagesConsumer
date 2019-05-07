package consumer_app.main;

import consumer_app.common.Constants;
import consumer_app.common.Utils;
import consumer_app.consumer.Consumer;
import consumer_app.consumer.ValuesMessagesListener;
import consumer_app.consumer.exceptions.ConfigurationException;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Class.forName("web_app.repository.db.db_models.ResultModel");

            Properties config = getConfigProperties();

            Consumer consumer = new Consumer(config);

            consumer.createConnection();
            consumer.setMessageListener(new ValuesMessagesListener(config));
        } catch (ClassNotFoundException e) {
            logger.error("Class 'web_app.repository.db.db_models.ResultModel' not found.");
        } catch (IOException ex) {
            logger.error("Config file {} not found.", System.getenv(Constants.ENV_VAR_NAME));
        } catch (SQLException ex) {
            logger.error("SQLConnection has been thrown", ex);
        } catch (ConfigurationException | JMSException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @NotNull
    private static Properties getConfigProperties() throws IOException {
        String filename = System.getenv(Constants.ENV_VAR_NAME);
        Reader configFileReader = new FileReader(filename);

        Properties properties = new Properties();

        Ini configFile = new Ini();
        configFile.load(configFileReader);

        Utils.addAllConfigParamsFromSection(configFile, Constants.JDBC_CFG, properties);
        Utils.addAllConfigParamsFromSection(configFile, Constants.ACTIVE_MQ_CFG, properties);
        Utils.addAllConfigParamsFromSection(configFile, Constants.CACHE_CFG, properties);

        return properties;
    }
}

package consumer_app.consumer;

import consumer_app.common.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Consume;

import javax.jms.*;
import java.util.Properties;

public class Consumer {

    public interface MessageListener {
        void onMessageReceived(Message message);
    }

    private Connection connection;
    private Session session;
    private String clientName;
    private String queueName;
    private String brokerUrl;

    private MessageListener messageListener;

    public Consumer (Properties properties) throws Exception {

        this.clientName = getProperty(properties, Constants.ACTIVE_MQ_CLIENT_ID);
        this.queueName = getProperty(properties, Constants.ACTIVE_MQ_QUEUE_NAME);
        this.brokerUrl = getProperty(properties, Constants.ACTIVE_MQ_BROKER_URL);
    }

    private String getProperty(Properties properties, String name) throws Exception {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new Exception(String.format(
                    "Missing required config property '%s'",
                    name));
        }

        return value;
    }

    public void createConnection() throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        connection = connectionFactory.createConnection();
        connection.setClientID(clientName);
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(queueName);

        MessageConsumer consumer = session.createConsumer(queue);

        consumer.setMessageListener(message -> {
            if (messageListener != null) {
                messageListener.onMessageReceived(message);
            }
        });
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void closeConnection() throws JMSException {
        session.close();
        connection.close();
    }
}

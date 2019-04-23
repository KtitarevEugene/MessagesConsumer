package consumer_app.consumer;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Consumer {

    public interface MessageListener {
        void onMessageReceived(Message message);
    }

    private Connection connection;
    private Session session;

    private MessageListener messageListener;

    public void createConnection (String clientName, String queueName) throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);

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

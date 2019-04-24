package consumer_app.main;

import consumer_app.common.Constants;
import consumer_app.consumer.Consumer;
import consumer_app.consumer.ValuesMessagesListener;

import javax.jms.JMSException;

public class Main {

    public static void main(String[] args) throws JMSException {
        Consumer consumer = new Consumer();

        consumer.createConnection(Constants.CLIENT_ID, Constants.QUEUE_NAME);
        consumer.setMessageListener(new ValuesMessagesListener());
    }
}
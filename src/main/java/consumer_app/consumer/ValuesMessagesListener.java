package consumer_app.consumer;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import consumer_app.common.Constants;
import consumer_app.db.MySQLConnector;
import consumer_app.db.models.ResultModel;
import consumer_app.prime_numbers.PrimesSearchFactory;
import consumer_app.prime_numbers.strategies_context.PrimesSearch;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.List;

public class ValuesMessagesListener implements Consumer.MessageListener {

    @Override
    public void onMessageReceived(Message message) {
        try {
            processReceivedMessage(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void processReceivedMessage(Message message) throws JMSException {
        Integer value = getMessageValue(message);
        if (value != null) {
            List<Integer> primeNumbers = getPrimeNumbers(value);

            String queueId = message.getJMSMessageID();

            ResultModel model = createModel(value, numbersListToString(primeNumbers, ", "), queueId);

            insertDataToDatabase(model);
        }
    }

    @Nullable
    private Integer getMessageValue(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String value = textMessage.getText();

                return Integer.parseInt(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @NotNull
    private List<Integer> getPrimeNumbers(int value) {
        PrimesSearch primesSearch = PrimesSearchFactory.simpleSearchAlgorithm();
        return primesSearch.getPrimeNumbers(value);
    }

    private String numbersListToString(List<Integer> numbers, String separator) {
        return numbers.stream()
                .map(String::valueOf)
                .reduce("", (v1, v2) -> v1.isEmpty() ? v1 + v2 : v1 + separator + v2);
    }

    private ResultModel createModel (int value, String primeNumbersLine, String queueId) {
        ResultModel model = new ResultModel();

        model.setValue(String.valueOf(value));
        model.setPrimeNumbers(primeNumbersLine);
        model.setQueueId(queueId);

        return model;
    }

    private void insertDataToDatabase(ResultModel resultModel) {
        try {
            try (MySQLConnector connector = new MySQLConnector(Constants.DB_USER, Constants.DB_PASSWORD)) {
                List<ResultModel> resultModels = connector.getResultByValue(resultModel.getValue());
                if (resultModels != null && resultModels.isEmpty()) {
                    connector.insertResultModel(resultModel);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package consumer_app.consumer;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import consumer_app.common.Constants;
import consumer_app.db.MySQLConnector;
import consumer_app.db.models.ResultModel;
import consumer_app.prime_numbers.PrimesSearchFactory;
import consumer_app.prime_numbers.strategies_context.PrimesSearch;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.List;

public class ValuesMessagesListener implements Consumer.MessageListener {

    @Override
    public void onMessageReceived(Message message) {
        processReceivedMessage(message);
    }

    private void processReceivedMessage(Message message) {
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

    private ResultModel getResultModelById(int id) {
        try (MySQLConnector connector = new MySQLConnector(Constants.DB_USER, Constants.DB_PASSWORD)) {
            return connector.getResultById(id);
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

    private void addResultToRequestedValue(ResultModel resultModel) {
        try (MySQLConnector connector = new MySQLConnector(Constants.DB_USER, Constants.DB_PASSWORD)) {
            connector.updateResultModel(resultModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

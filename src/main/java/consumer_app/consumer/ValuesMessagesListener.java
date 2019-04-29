package consumer_app.consumer;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import consumer_app.common.Constants;
import consumer_app.repository.DataRepository;
import consumer_app.repository.cache.cache_managers.MemcachedManager;
import consumer_app.repository.db.db_managers.MySQLConnectorManager;
import consumer_app.prime_numbers.PrimesSearchFactory;
import consumer_app.prime_numbers.strategies_context.PrimesSearch;
import web_app.repository.db.db_models.ResultModel;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Properties;

public class ValuesMessagesListener implements Consumer.MessageListener {

    private DataRepository dataRepository;

    public ValuesMessagesListener(Properties properties) {
        MemcachedManager memcachedManager = new MemcachedManager.Builder()
                .setHost(properties.getProperty(Constants.CACHE_HOST))
                .setPort(Integer.parseInt(properties.getProperty(Constants.CACHE_PORT)))
                .setOperationTimeoutMillis(Integer.parseInt(properties.getProperty(Constants.CACHE_TIMEOUT)))
                .setExpirationTimeMillis(Integer.parseInt(properties.getProperty(Constants.CACHE_EXPIRATION_TIME)))
                .build();

        dataRepository = new DataRepository(
                new MySQLConnectorManager(properties),
                memcachedManager);
    }

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

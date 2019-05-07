package consumer_app.consumer.exceptions;

public class ConfigurationException extends Exception {
    public ConfigurationException() { super(); }

    public ConfigurationException(String what) { super(what); }

    public ConfigurationException(Throwable cause) { super(cause); }

    public ConfigurationException(String what, Throwable cause) { super(what, cause); }
}

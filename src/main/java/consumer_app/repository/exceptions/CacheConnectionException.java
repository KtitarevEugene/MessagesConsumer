package consumer_app.repository.exceptions;

public class CacheConnectionException extends Exception {
    public CacheConnectionException() {}

    public CacheConnectionException(String what) {
        super(what);
    }
}

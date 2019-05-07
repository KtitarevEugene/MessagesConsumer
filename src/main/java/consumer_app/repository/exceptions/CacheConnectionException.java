package consumer_app.repository.exceptions;

public class CacheConnectionException extends Exception {
    public CacheConnectionException() { super(); }

    public CacheConnectionException(String what) { super(what); }

    public CacheConnectionException(Throwable cause) { super(cause); }

    public CacheConnectionException(String what, Throwable cause) { super(what, cause); }
}

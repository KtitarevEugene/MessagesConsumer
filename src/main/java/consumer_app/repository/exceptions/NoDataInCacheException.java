package consumer_app.repository.exceptions;

public class NoDataInCacheException extends Exception {
    public NoDataInCacheException() { super(); }

    public NoDataInCacheException(String what) { super(what); }

    public NoDataInCacheException(Throwable cause) { super(cause); }

    public NoDataInCacheException(String what, Throwable cause) { super(what, cause); }

}

package consumer_app.repository.exceptions;

public class NoDataInCacheException extends Exception {
    public NoDataInCacheException() {}

    public NoDataInCacheException(String what) {
        super(what);
    }
}

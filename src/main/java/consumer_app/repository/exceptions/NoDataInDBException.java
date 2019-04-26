package consumer_app.repository.exceptions;

public class NoDataInDBException extends Exception {

    public NoDataInDBException() {}

    public NoDataInDBException(String what) {
        super(what);
    }
}

package consumer_app.repository.exceptions;

public class NoDataInDBException extends Exception {
    public NoDataInDBException() { super(); }

    public NoDataInDBException(String what) { super(what); }

    public NoDataInDBException(Throwable cause) { super(cause); }

    public NoDataInDBException(String what, Throwable cause) { super(what, cause); }

}

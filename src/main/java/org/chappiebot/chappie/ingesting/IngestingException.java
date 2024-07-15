package org.chappiebot.chappie.ingesting;

public class IngestingException extends Exception {

    public IngestingException() {
    }

    public IngestingException(String message) {
        super(message);
    }

    public IngestingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IngestingException(Throwable cause) {
        super(cause);
    }

    public IngestingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}

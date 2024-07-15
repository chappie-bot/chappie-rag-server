package org.chappiebot.chappie.document;

public class DocumentFetcherException extends Exception {

    public DocumentFetcherException() {
    }

    public DocumentFetcherException(String message) {
        super(message);
    }

    public DocumentFetcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentFetcherException(Throwable cause) {
        super(cause);
    }

    public DocumentFetcherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}

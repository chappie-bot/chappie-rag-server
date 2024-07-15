package org.chappiebot.chappie.summary;

public class SummaryException extends Exception {

    public SummaryException() {
    }

    public SummaryException(String message) {
        super(message);
    }

    public SummaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SummaryException(Throwable cause) {
        super(cause);
    }

    public SummaryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    
}

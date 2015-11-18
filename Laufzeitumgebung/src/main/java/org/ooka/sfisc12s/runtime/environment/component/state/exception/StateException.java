package org.ooka.sfisc12s.runtime.environment.component.state.exception;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateException extends Exception {

    public StateException() {
    }

    public StateException(String message) {
        super(message);
    }

    public StateException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateException(Throwable cause) {
        super(cause);
    }

    public StateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

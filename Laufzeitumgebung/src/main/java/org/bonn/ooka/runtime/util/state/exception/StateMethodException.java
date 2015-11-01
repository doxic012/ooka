package org.bonn.ooka.runtime.util.state.exception;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateMethodException extends Exception {

    public StateMethodException() {
    }

    public StateMethodException(String message) {
        super(message);
    }

    public StateMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateMethodException(Throwable cause) {
        super(cause);
    }

    public StateMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

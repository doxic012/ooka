package org.bonn.ooka.runtime.util.exception;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateMethodNotAllowedException extends Exception {

    public StateMethodNotAllowedException() {
    }

    public StateMethodNotAllowedException(String message) {
        super(message);
    }

    public StateMethodNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateMethodNotAllowedException(Throwable cause) {
        super(cause);
    }

    public StateMethodNotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

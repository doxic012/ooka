package org.ooka.sfisc12s.runtime.environment.component.state.exception;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateException extends Exception {

    public StateException() {
    }

    public StateException(String message, Object... args) {
        super(String.format(message, args));
    }

    public StateException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }

    public StateException(Throwable cause) {
        super(cause);
    }

    public StateException(Throwable cause, String message, boolean enableSuppression, boolean writableStackTrace, Object... args) {
        super(String.format(message, args), cause, enableSuppression, writableStackTrace);
    }
}

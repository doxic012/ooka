package org.ooka.sfisc12s.runtime.environment.scope.exception;

/**
 * Created by Stefan on 26.10.2015.
 */
public class ScopeException extends Exception {

    public ScopeException() {
    }

    public ScopeException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ScopeException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }

    public ScopeException(Throwable cause) {
        super(cause);
    }

    public ScopeException(Throwable cause, String message, boolean enableSuppression, boolean writableStackTrace, Object... args) {
        super(String.format(message, args), cause, enableSuppression, writableStackTrace);
    }
}

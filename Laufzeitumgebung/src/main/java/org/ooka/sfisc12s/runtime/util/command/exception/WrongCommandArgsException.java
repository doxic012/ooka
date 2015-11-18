package org.ooka.sfisc12s.runtime.util.command.exception;

/**
 * Created by Stefan on 26.10.2015.
 */
public class WrongCommandArgsException extends Exception {
    public WrongCommandArgsException() {
    }

    public WrongCommandArgsException(String message) {
        super(message);
    }

    public WrongCommandArgsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongCommandArgsException(Throwable cause) {
        super(cause);
    }

    public WrongCommandArgsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package org.ooka.sfisc12s.runtime.util.Logger;

public interface Logger {

    void debug(String text);

    void debug(String formattedText, Object... args);

    void error(String text);

    void error(String formattedText, Object... args);

    void error(Throwable ex);

    void error(Throwable ex, String text);

    void error(Throwable ex, String formattedText, Object...args);

}

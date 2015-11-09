package org.bonn.ooka.runtime.util.Logger;

public interface Logger {

    void debug(String text);

    void debug(String formattedText, Object... args);

    void error(Exception ex);

    void error(Exception ex, String text);

    void error(Exception ex, String formattedText, Object...args);

}

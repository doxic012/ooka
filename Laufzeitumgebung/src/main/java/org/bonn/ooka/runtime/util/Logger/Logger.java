package org.bonn.ooka.runtime.util.Logger;

public interface Logger {

    void log(String text);

    void log(String formattedText, Object... args);

    void error(Exception ex, String text);

    void error(Exception ex, String formattedText, Object...args);

}

package org.bonn.ooka.service;

/**
 * Created by steve on 06.10.15.
 */
public interface Logger {
    void error(String msg, Exception ex);

    void debug(String msg);
}

package org.bonn.ooka.BuchungsClient.service;

/**
 * Created by steve on 06.10.15.
 */
public class SystemLogger implements Logger {
    @Override
    public void error(String msg, Exception ex) {
        System.err.println(String.format("%s, %s", msg, ex.getMessage()));
        ex.printStackTrace();
    }

    @Override
    public void debug(String msg) {
        System.out.println(msg);
    }
}

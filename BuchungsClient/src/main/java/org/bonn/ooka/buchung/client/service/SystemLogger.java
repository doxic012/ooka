package org.bonn.ooka.buchung.client.service;

import org.bonn.ooka.buchung.system.service.Logger;

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

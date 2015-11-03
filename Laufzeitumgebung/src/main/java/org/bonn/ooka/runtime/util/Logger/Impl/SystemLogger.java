package org.bonn.ooka.runtime.util.Logger.Impl;

import org.bonn.ooka.runtime.util.Logger.Logger;

import java.time.LocalTime;

/**
 * Created by steve on 03.11.15.
 */
public class SystemLogger implements Logger {
    @Override
    public void debug(String text) {
        System.out.println(String.format("+++ Runtime-Log: %s (%s)%s", text, LocalTime.now().toString(), System.lineSeparator()));
    }
}

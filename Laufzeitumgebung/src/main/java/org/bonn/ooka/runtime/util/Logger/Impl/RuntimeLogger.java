package org.bonn.ooka.runtime.util.Logger.Impl;

import org.bonn.ooka.runtime.util.Logger.Logger;

import java.time.LocalTime;

/**
 * Created by steve on 03.11.15.
 */
public class RuntimeLogger implements Logger {

    @Override
    public void log(String text) {
        System.out.println(String.format("+++ Runtime-Log: %s (%s)", text, LocalTime.now().toString()));
    }

    @Override
    public void log(String formattedText, Object... args) {
        log(String.format(formattedText, args));
    }

    @Override
    public void error(Exception ex, String text) {
        System.out.println(String.format("+++ Runtime-Log Error: %s (%s)", text, LocalTime.now().toString()));
        System.out.println(String.format("+++ %s", ex.getMessage()));
        System.out.println(String.format("+++ %s", ex.getStackTrace()));
    }

    @Override
    public void error(Exception ex, String formattedText, Object... args) {
        error(ex, String.format(formattedText, args));
    }
}

package org.ooka.sfisc12s.runtime.util.Logger.Impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.time.LocalTime;

/**
 * Created by steve on 03.11.15.
 */
public class RuntimeLogger implements Logger {

    private Class<?> clazz;

    public RuntimeLogger(Class<?> clazz) {
        this.clazz = clazz;
    }

    public RuntimeLogger() {
    }

    private String getClassName() {
        return clazz != null ? " (" + clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1) + ") " : "";
    }

    @Override
    public void debug(String text) {
        System.out.println(String.format("+++ Runtime-Log%s: %s (%s)", getClassName(), text, LocalTime.now().toString()));
    }

    @Override
    public void debug(String formattedText, Object... args) {
        debug(String.format(formattedText, args));
    }

    @Override
    public void error(Throwable ex) {
        error(ex, "Exception was thrown.");
    }

    @Override
    public void error(Throwable ex, String text) {
        System.out.println(String.format("+++ Runtime-Log Exception%s: %s (%s)", getClassName(), text, LocalTime.now().toString()));
        System.out.println(String.format("+++ %s", ex.getMessage()));
        System.out.println(String.format("+++ %s", ex.getStackTrace()));
    }

    @Override
    public void error(Throwable ex, String formattedText, Object... args) {
        error(ex, String.format(formattedText, args));
    }
}

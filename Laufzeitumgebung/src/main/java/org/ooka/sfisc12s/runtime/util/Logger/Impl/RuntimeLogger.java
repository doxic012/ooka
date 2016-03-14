package org.ooka.sfisc12s.runtime.util.Logger.Impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.time.LocalTime;
import java.util.Arrays;

public class RuntimeLogger implements Logger {

    private Class<?> clazz;

    public RuntimeLogger(Class<?> clazz) {
        this.clazz = clazz;
    }

    public RuntimeLogger() {
    }

    private String getClassName() {
        return clazz != null ? String.format(", %s", clazz.getSimpleName()) : "";
    }

    private void print(String text, String logType) {
        System.out.println(String.format("+++ Runtime-%s (%s%s): %s", logType, LocalTime.now().toString(), getClassName(), text));
    }

    @Override
    public void debug(String text) {
        print(text, "log");
    }

    @Override
    public void debug(String formattedText, Object... args) {
        debug(String.format(formattedText, args));
    }

    @Override
    public void error(String text) {
        print(text, "error");
    }

    @Override
    public void error(Throwable ex) {
        error(ex, "Exception was thrown.");
    }

    @Override
    public void error(Throwable ex, String text) {
        print(text, "error");
        System.out.println(String.format("+++ %s", ex.getMessage()));
        System.out.println(String.format("+++ %s", Arrays.toString(ex.getStackTrace())));
        ex.printStackTrace(System.err);
    }

    @Override
    public void error(Throwable ex, String formattedText, Object... args) {
        error(ex, String.format(formattedText, args));
    }
}

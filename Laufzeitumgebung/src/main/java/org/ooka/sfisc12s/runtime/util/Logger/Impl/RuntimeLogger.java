package org.ooka.sfisc12s.runtime.util.Logger.Impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.io.PrintStream;
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

    private void print(PrintStream stream, String text, String logType) {
        stream.println(String.format("+++ Runtime-%s (%s%s): %s", logType, LocalTime.now().toString(), getClassName(), text));
    }

    @Override
    public void debug(String text) {
        print(System.out, text, "log");
    }

    @Override
    public void debug(String formattedText, Object... args) {
        debug(String.format(formattedText, args));
    }

    @Override
    public void error(String text) {
        print(System.err, text, "error");
    }

    @Override
    public void error(String formattedText, Object... args) {
        error(String.format(formattedText, args));
    }

    @Override
    public void error(Throwable ex) {
        error(ex, "Exception was thrown.");
    }

    @Override
    public void error(Throwable ex, String text) {
        error(text);
        System.err.println(String.format("+++ %s", ex.getMessage()));
        ex.printStackTrace();
    }

    @Override
    public void error(Throwable ex, String formattedText, Object... args) {
        error(ex, String.format(formattedText, args));
    }
}

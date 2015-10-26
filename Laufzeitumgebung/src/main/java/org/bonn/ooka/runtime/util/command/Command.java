package org.bonn.ooka.runtime.util.command;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Stefan on 24.10.2015.
 */
public class Command<T> {
    private String name;

    private String args;

    private Consumer<T> method;

    public Command(String name, String args, Consumer<T> method) {
        this.name = name;
        this.args = args;
        this.method = method;
    }
    public Command(String name, Consumer<T> method) {
        this(name, "", method);
    }

    public Consumer<T> getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public String getArgs() {
        return args;
    }
}

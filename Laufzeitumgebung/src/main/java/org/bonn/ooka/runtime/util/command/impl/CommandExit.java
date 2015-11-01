package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.util.command.Command;

import java.util.function.Consumer;
import java.util.function.Function;

public class CommandExit extends Command<String> {

    private String name;

    public CommandExit(String name) {
        this.name = name;
    }

    @Override
    public Consumer getMethod() {
        return (p) -> {
            System.exit(0);
        };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s':\t%s",
                getName(),
                "Exit the runtime environment.\n");
    }
}

package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.command.Command;

import java.util.function.Consumer;

public class CommandExit extends Command<String> {

    public CommandExit(String name) {
        super(name, "");
    }

    @Override
    public Consumer getMethod() {
        return (p) -> {
            System.exit(0);
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s':\t%s",
                getName(),
                "Exit the runtime environment.\n");
    }
}

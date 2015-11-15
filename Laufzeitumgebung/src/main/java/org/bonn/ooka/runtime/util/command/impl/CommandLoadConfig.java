package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.command.CommandStation;

import java.util.function.Consumer;

public class CommandLoadConfig extends Command<String> {

    private CommandStation commandStation;
    public CommandLoadConfig(String name, RuntimeEnvironment re, CommandStation comm) {
        super(name, "", re);
        commandStation = comm;
    }

    @Override
    public Consumer<String> getMethod() {
        return (config -> {
            commandStation.loadConfig(config);
        });
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s':\t%s",
                getName(),
                "Load a config into the runtime environment.\n");
    }
}

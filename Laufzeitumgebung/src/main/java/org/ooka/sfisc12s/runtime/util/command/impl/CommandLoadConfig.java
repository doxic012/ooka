package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.util.command.CommandStation;

import java.util.function.Consumer;
import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

public class CommandLoadConfig extends Command<String> {

    private CommandStation commandStation;
    public CommandLoadConfig(String name, CommandStation comm) {
        super(name, DEFAULT_ARGS);
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

package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.util.command.CommandStation;

import java.util.function.Consumer;
import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

public class CommandLoadConfig extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandLoadConfig.class);

    private CommandStation commandStation;

    public CommandLoadConfig(String name, CommandStation comm) {
        super(name, DEFAULT_ARGS);
        commandStation = comm;
    }

    @Override
    public Consumer<String> getMethod() {
        return commandStation::loadConfig;
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s':\t%s",
                getName(),
                "Load a config into the runtime environment.\n");
    }
}

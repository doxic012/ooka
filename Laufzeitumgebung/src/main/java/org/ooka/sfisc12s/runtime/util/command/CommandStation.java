package org.ooka.sfisc12s.runtime.util.command;

import javafx.util.Pair;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.PatternMap;
import org.ooka.sfisc12s.runtime.util.command.exception.CommandNotFoundException;
import org.ooka.sfisc12s.runtime.util.command.exception.WrongCommandArgsException;
import org.ooka.sfisc12s.runtime.util.command.impl.CommandExit;

import java.io.IOException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStation {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandStation.class);

    private Config config;

    private PatternMap<Command> commandMap = new PatternMap<>();

    public CommandStation(String configPath) {
        this.config = new Config(configPath);
    }

    public CommandStation loadConfig(String configPath) {
        try {
            if (configPath == null || configPath.isEmpty()) {
             log.debug("Invalid config path");
                return this;
            }
            Config cfg = new Config(configPath);
            log.debug("Loading config: %s", cfg.getPath());

            for (String entry : cfg.getConfig())
                executeCommandRaw(entry);

            log.debug("Config '%s' loaded.", configPath);
        } catch (IOException | WrongCommandArgsException | CommandNotFoundException e) {
            e.printStackTrace();
        }

        return this;
    }

    public CommandStation addCommand(Command<?> command) {
        commandMap.putIfAbsent(command.getName(), new Pair(command.getArgs(), command));
        return this;
    }

    public CommandStation removeCommand(Command command) {
        commandMap.computeIfPresent(command.getName(), (cmd, action) -> commandMap.remove(cmd));
        return this;
    }

    public PatternMap<Command> getCommands() {
        return commandMap;
    }

    public void printCommands() {
        log.debug("Allowed commands:");
        getCommands().keySet().stream().forEachOrdered(log::debug);
    }

    public void executeCommandRaw(String command) throws CommandNotFoundException, WrongCommandArgsException, IOException {
        Pair<String, Command> match = commandMap.getMatchingPair(command);

        // Execute command with arguments
        if (match != null)
            match.getValue().getMethod().accept(match.getKey().trim());
    }

    public void executeCommand(String command) throws CommandNotFoundException, WrongCommandArgsException, IOException {
        Pair<String, Command> match = commandMap.getMatchingPair(command);

        // try to evaluate potential command without last phrase (args?)
        if (match == null) {
            int lastArgs;
            String commandArgs = command;
            while ((lastArgs = commandArgs.lastIndexOf(' ')) != -1) {
                commandArgs = commandArgs.substring(0, lastArgs);
                Command cmd = commandMap.getMatchingCommand(commandArgs);

                if (cmd != null)
                    throw new WrongCommandArgsException(String.format("Wrong arguments for command '%s'.\n%s", commandArgs, cmd.getCommandDescription()));
            }

            throw new CommandNotFoundException(String.format("Command '%s' not found.\n", command));
        }
        // Execute command with arguments
        match.getValue().getMethod().accept(match.getKey().trim());

        if (!(match.getValue() instanceof CommandExit))
            config.writeToConfig(command);
    }
}

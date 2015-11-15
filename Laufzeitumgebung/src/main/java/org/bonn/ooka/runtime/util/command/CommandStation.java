package org.bonn.ooka.runtime.util.command;

import javafx.util.Pair;
import org.bonn.ooka.runtime.util.PatternMap;
import org.bonn.ooka.runtime.util.command.exception.CommandNotFoundException;
import org.bonn.ooka.runtime.util.command.exception.WrongCommandArgsException;
import org.bonn.ooka.runtime.util.command.impl.CommandExit;

import java.io.IOException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStation {

    private Config config;

    private PatternMap<Command> commandMap = new PatternMap<>();

    public CommandStation(String configPath) {
        this.config = new Config(configPath);
    }

    public CommandStation loadConfig() {
        return loadConfig(this.config.getPath());
    }

    public CommandStation loadConfig(String configPath) {
        try {
            config = new Config(configPath);
            System.out.println("Loading config: " + config.getPath());

            for (String entry : config.getConfig())
                executeCommandRaw(entry);

            System.out.println("Config loaded.");
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
        System.err.println("Allowed commands:");
        getCommands().keySet().stream().forEachOrdered(System.out::println);
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

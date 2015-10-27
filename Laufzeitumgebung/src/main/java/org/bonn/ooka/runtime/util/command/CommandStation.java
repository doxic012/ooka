package org.bonn.ooka.runtime.util.command;

import javafx.util.Pair;
import org.bonn.ooka.runtime.util.PatternMap;
import org.bonn.ooka.runtime.util.exception.CommandNotFoundException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStation {
    private PatternMap<Command> commandMap = new PatternMap<>();

    public CommandStation addCommand(Command<?> command) {
        commandMap.putIfAbsent(command.getName(), new Pair(command.getArgs(), command));

//        this.addCompleter((buf, cursor, candidates) -> {
//            if (buf.equals(commandPattern) || (buf.length() <= commandPattern.length()) && buf.equals(commandPattern.substring(0, buf.length())))
//                candidates.add(commandPattern);
//
//            return candidates.isEmpty() ? -1 : 0;
//        });
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
        getCommands().keySet().stream().forEachOrdered(System.err::println);
    }

    public void executeCommand(String command) throws CommandNotFoundException {
        Pair<String, Command> match = commandMap.getMatchingPair(command);

        // Execute command with arguments
        if (match != null)
            match.getValue().getMethod().accept(match.getKey().trim());
        else
            throw new CommandNotFoundException(String.format("Command '%s' not found or wrong arguments", command));
    }
}

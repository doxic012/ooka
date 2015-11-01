package org.bonn.ooka.runtime.util.command;

import javafx.util.Pair;
import org.bonn.ooka.runtime.util.PatternMap;
import org.bonn.ooka.runtime.util.command.exception.CommandNotFoundException;
import org.bonn.ooka.runtime.util.command.exception.WrongCommandArgsException;

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
        getCommands().keySet().stream().forEachOrdered(System.out::println);
    }

    public void executeCommand(String command) throws CommandNotFoundException, WrongCommandArgsException {
        Pair<String, Command> match = commandMap.getMatchingPair(command);

        // try to evaluate potential command without last phrase (args?)
        if (match == null) {
            int lastArgs;

            while ((lastArgs = command.lastIndexOf(' ')) != -1) {
                command = command.substring(0, lastArgs);
                Command cmd = commandMap.getMatchingCommand(command);

                if (cmd != null)
                    throw new WrongCommandArgsException(String.format("Wrong arguments for command '%s'.\n%s", command, cmd.getCommandDescription()));
            }

            throw new CommandNotFoundException(String.format("Command '%s' not found.\n", command));
        }
        // Execute command with arguments
        if (match != null)
            match.getValue().getMethod().accept(match.getKey().trim());
    }
}

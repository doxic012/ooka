package org.bonn.ooka.runtime.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javafx.util.Pair;
import org.bonn.ooka.runtime.util.Command;
import org.bonn.ooka.runtime.util.PatternMap;
import org.bonn.ooka.runtime.util.State;
import org.bonn.ooka.runtime.util.Stateful;

public class RuntimeEnvironment implements Stateful {

    private PatternMap<Command> commandMap = new PatternMap<>();

    private State state = State.Stopped;

    public RuntimeEnvironment() {
        addCommand("(quit)|(exit)", (p) -> stop());
    }

    public RuntimeEnvironment addCommand(String commandPattern, String args, Command<String> commandAction) {
        commandMap.putIfAbsent(commandPattern, new Pair(args, commandAction));

//        this.addCompleter((buf, cursor, candidates) -> {
//            if (buf.equals(commandPattern) || (buf.length() <= commandPattern.length()) && buf.equals(commandPattern.substring(0, buf.length())))
//                candidates.add(commandPattern);
//
//            return candidates.isEmpty() ? -1 : 0;
//        });
        return this;
    }

    public RuntimeEnvironment addCommand(String commandPattern, Command<String> commandAction) {
        addCommand(commandPattern, "", commandAction);

        return this;
    }

    public RuntimeEnvironment removeCommand(String commandPattern) {
        commandMap.computeIfPresent(commandPattern, (cmd, action) -> commandMap.remove(cmd));
        return this;
    }

    public void start() {
        setState(State.Started);
        Scanner scan = new Scanner(System.in);
        String line;

        while (getState() != State.Stopped && (line = scan.nextLine()) != null) {
            Pair<String, Command> match = commandMap.getMatchingPair(line);

            if (match != null)
                match.getValue().execute(match.getKey());
        }
    }

    public void stop() {
        setState(State.Stopped);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
}

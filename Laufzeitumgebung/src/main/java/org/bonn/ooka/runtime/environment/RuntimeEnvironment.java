package org.bonn.ooka.runtime.environment;

import java.util.Scanner;

import javafx.util.Pair;
import org.bonn.ooka.runtime.util.Command;
import org.bonn.ooka.runtime.util.PatternMap;
import org.bonn.ooka.runtime.util.State;
import org.bonn.ooka.runtime.util.Stateful;

public class RuntimeEnvironment implements Stateful {
    public static class Arguments {

        private static String WORD_BASE = "\\w\\_\\-\\:\\(\\)\\.\\/\\\\öäüÖÄÜ";
        public static String EXT (String extension) {
            return String.format("(\\.%s)", extension);
        }

        public static String WORD (String ext){
            return String.format("[%s]+%s", WORD_BASE, EXT(ext));
        }

        public static String QUOTED_WORD (String ext) {
            return String.format("\"[%s\\s]+%s\"", WORD_BASE, EXT(ext));
        }

        public static String WORD_OR_QUOTED (String ext) {
            return String.format("(%s|%s)", WORD(ext), QUOTED_WORD(ext));
        }

        public static String MODIFIED_ARGS (String ext){
            return String.format("(\\s+%s(,\\s*%s)*)?", WORD_OR_QUOTED(ext), WORD_OR_QUOTED(ext));
        }

        public static String DEFAULT_ARGS = MODIFIED_ARGS("\\w+");
    }

    private PatternMap<Command> commandMap = new PatternMap<>();

    private State state = State.Stopped;

    public RuntimeEnvironment() {
        addCommand("(quit)|(exit)", "", (p) -> stop());
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
        addCommand(commandPattern, Arguments.DEFAULT_ARGS, commandAction);

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
                match.getValue().execute(match.getKey().trim());
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

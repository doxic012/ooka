package org.bonn.ooka.runtime.environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import jline.Terminal;
import jline.console.ConsoleReader;
import org.bonn.ooka.runtime.util.Command;
import org.bonn.ooka.runtime.util.State;
import org.bonn.ooka.runtime.util.Stateful;

public class RuntimeEnvironment extends ConsoleReader implements Stateful {

    private Map<String, Command<String>> commandMap = new HashMap<>();

    private State state = State.Stopped;

    public RuntimeEnvironment() throws IOException {
    }

    public RuntimeEnvironment(InputStream in, OutputStream out) throws IOException {
        super(in, out);
    }

    public RuntimeEnvironment(InputStream in, OutputStream out, Terminal term) throws IOException {
        super(in, out, term);
    }

    public RuntimeEnvironment addCommand(String command, Command<String> commandAction) {
        commandMap.putIfAbsent(command, commandAction);

        this.addCompleter((buf, cursor, candidates) -> {
            if (buf.equals(command) || (buf.length() <= command.length()) && buf.equals(command.substring(0, buf.length())))
                candidates.add(command);

            return candidates.isEmpty() ? -1 : 0;
        });
        return this;
    }

    public RuntimeEnvironment removeCommand(String command) {
        commandMap.computeIfPresent(command, (cmd, action) -> commandMap.remove(cmd));
        return this;
    }


    public void start() throws IOException {
        setState(State.Started);
        String line;

        while (getState() != State.Stopped && (line = readLine()) != null) {
            getMatch(line);
        }
    }

    public void stop() {
        setState(State.Stopped);
    }

    public void getMatch(String input) {
        final String match = input.endsWith(" ") ? input.substring(0, input.length() - 2) : input;

        commandMap.forEach((command, action) -> {
            if (match.equals(command))
                action.execute(null);
        });
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

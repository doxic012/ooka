package org.bonn.ooka.runtime.util.command;

import java.util.function.Function;

public class CommandExit extends Command<String>{
    public CommandExit(String name, String args) {
        super(name, args, (p) -> {System.exit(0);});
    }

    public CommandExit(String name) {
        this(name, "");
    }
}

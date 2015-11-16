package org.bonn.ooka.runtime.util.command;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
import org.bonn.ooka.runtime.util.Logger.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Stefan on 24.10.2015.
 */
public abstract class Command<T> {

    private static Logger log = LoggerFactory.getRuntimeLogger(Command.class);

    private String args;

    private String name;

    public Command(String name, String args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public String getArgs() {
        return args;
    }

    public abstract Consumer<T> getMethod();

    public abstract String getCommandDescription();

    protected String verifyArguments(String args) {
        if (args == null || args.length() == 0) {
            System.out.print(">> ");

            Scanner scan = new Scanner(System.in);
            args = scan.nextLine();
        }

        // replace all backslash with slash
        return args.replaceAll("\\\\", "/");
    }

    public Logger getLogger() {
        return log;
    }
}
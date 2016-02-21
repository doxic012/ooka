package org.ooka.sfisc12s.runtime.util.command;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Created by Stefan on 24.10.2015.
 */
public abstract class Command<T> {

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
}
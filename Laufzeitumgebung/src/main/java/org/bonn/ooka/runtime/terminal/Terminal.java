package org.bonn.ooka.runtime.terminal;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.command.*;
import org.bonn.ooka.runtime.util.command.exception.WrongCommandArgsException;
import org.bonn.ooka.runtime.util.command.impl.*;
import org.bonn.ooka.runtime.util.command.exception.CommandNotFoundException;
import org.bonn.ooka.runtime.environment.annotation.StartMethod;

import java.io.IOException;
import java.util.Scanner;

public class Terminal {

    private CommandStation commandStation;

    public Terminal(CommandStation station) {
        this.commandStation = station;
    }

    @StartMethod
    public void startTerminal() {
        Scanner scan = new Scanner(System.in);
        String line;

        while (true) {
            System.out.print("> ");

            if ((line = scan.nextLine()) == null)
                break;

            try {
                commandStation.executeCommand(line);
            } catch (CommandNotFoundException e) {
                System.out.println(e.getMessage());
                commandStation.printCommands();
            } catch (WrongCommandArgsException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        RuntimeEnvironment re = RuntimeEnvironment.getInstance();

        CommandStation comm = new CommandStation(System.getProperty("user.dir") + "/config.txt")
                .addCommand(new CommandExit("(quit)|(exit)", re))
                .addCommand(new CommandLoadClass("load class", re))
                .addCommand(new CommandLoadJar("load jar", re))
                .addCommand(new CommandLoadPath("load", re))
                .addCommand(new CommandUnloadClass("unload class", re))
                .addCommand(new CommandStart("start", re))
                .addCommand(new CommandStop("stop", re))
                .addCommand(new CommandGetStatus("get status", re))
                .loadConfig();
        try {
            new Terminal(comm).startTerminal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
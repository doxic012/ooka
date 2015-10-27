package org.bonn.ooka.runtime.environment;

import org.bonn.ooka.runtime.util.command.*;
import org.bonn.ooka.runtime.util.command.impl.*;
import org.bonn.ooka.runtime.util.exception.CommandNotFoundException;
import org.bonn.ooka.runtime.util.state.annotation.StartMethod;

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
                e.printStackTrace();
                commandStation.printCommands();
            }
        }
    }

    public static void main(String[] args) {
        RuntimeEnvironment re = RuntimeEnvironment.getInstance();

        CommandStation comm = new CommandStation()
                .addCommand(new CommandExit("(quit)|(exit)"))
                .addCommand(new CommandLoadClass("load class", re.getComponents(), re.getClassLoader()))
                .addCommand(new CommandLoadClassPath("load classpath", re.getClassLoader()))
                .addCommand(new CommandUnloadClass("unload class", re.getComponents()))
                .addCommand(new CommandStartClass("start class", re.getComponents()))
                .addCommand(new CommandStopClass("stop class", re.getComponents()))
                .addCommand(new CommandGetStatus("get status", re.getComponents()));

        try {
            new Terminal(comm).startTerminal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
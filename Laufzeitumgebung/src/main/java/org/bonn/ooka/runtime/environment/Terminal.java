package org.bonn.ooka.runtime.environment;

import org.bonn.ooka.runtime.util.command.*;
import org.bonn.ooka.runtime.util.command.exception.WrongCommandArgsException;
import org.bonn.ooka.runtime.util.command.impl.*;
import org.bonn.ooka.runtime.util.command.exception.CommandNotFoundException;
import org.bonn.ooka.runtime.util.annotation.StartMethod;

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

        CommandStation comm = new CommandStation(System.getProperty("user.dir")+"/config.txt")
                .addCommand(new CommandExit("(quit)|(exit)"))
                .addCommand(new CommandLoadClass("load class", re.getComponents(), re.getClassLoader()))
                .addCommand(new CommandLoadClassPath("load classpath", re.getClassLoader()))
                .addCommand(new CommandUnloadClass("unload class", re.getComponents()))
                .addCommand(new CommandStartClass("start class", re.getComponents()))
                .addCommand(new CommandStopClass("stop class", re.getComponents()))
                .addCommand(new CommandGetStatus("get status", re.getComponents()))
                .loadConfig();
        try {
            new Terminal(comm).startTerminal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
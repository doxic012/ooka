package org.ooka.sfisc12s.runtime.terminal;

import org.ooka.sfisc12s.runtime.util.command.impl.*;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.util.command.CommandStation;
import org.ooka.sfisc12s.runtime.util.command.exception.WrongCommandArgsException;
import org.ooka.sfisc12s.runtime.util.command.exception.CommandNotFoundException;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Long> fillMap() {
        Map<String, Long> map = new HashMap<>();

        for (int i = 0; i < 50; i++) {
            map.put("Key" + i, (long) i * 10000000);
        }
        return map;
    }

    public static Map<String, Long> copyMap(Map<String, Long> map) {
        Map<String, Long> clone = new HashMap<>();
        for (Map.Entry<String, Long> e : map.entrySet())
            clone.put(e.getKey(), e.getValue());

        return clone;
    }

    public static void test() {

        Map<String, Map<String, Long>> testMap = new HashMap<>();
        Map<String, Long> filler = fillMap();

        for (int i = 0; i < 50; i++) {
            testMap.put("Map" + i, copyMap(filler));
        }

        RuntimeEnvironment.measure(x -> testMap.
                entrySet().
                stream().
                map(Map.Entry::getValue).
                map(Map::entrySet).
                flatMap(Collection::stream).
                forEach(entry -> {
                    System.out.println("Entry key: " + entry.getKey() + ", value: " + entry.getValue() + ", id: " + entry.toString());
                }), 3).accept(null);


        RuntimeEnvironment.measure(x -> {
            for (Map.Entry<String, Map<String, Long>> mapEntry : testMap.entrySet())
                for (Map.Entry<String, Long> entry : mapEntry.getValue().entrySet())
                    System.out.println("Entry key: " + entry.getKey() + ", value: " + entry.getValue() + ", id: " + entry.toString());
        }).accept(null);
    }

    public static void main(String[] args) {
//        test();
        RuntimeEnvironment re = RuntimeEnvironment.getInstance();

        CommandStation comm = new CommandStation(System.getProperty("user.dir") + "/config.txt");
        comm.addCommand(new CommandExit("(quit)|(exit)"))
                .addCommand(new CommandLoadClass("load class"))
                .addCommand(new CommandLoadJar("load jar"))
                .addCommand(new CommandLoadPath("load"))
                .addCommand(new CommandUnload("unload"))
                .addCommand(new CommandStart("start"))
                .addCommand(new CommandStop("stop"))
                .addCommand(new CommandGetStatus("get status"))
                .addCommand(new CommandRemove("remove"))
                .addCommand(new CommandLoadConfig("load config", comm));

        new Terminal(comm).startTerminal();
    }
}
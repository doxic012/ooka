package org.ooka.sfisc12s.runtime.terminal;

import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;
import org.ooka.sfisc12s.runtime.util.command.impl.*;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.util.command.CommandStation;
import org.ooka.sfisc12s.runtime.util.command.exception.WrongCommandArgsException;
import org.ooka.sfisc12s.runtime.util.command.exception.CommandNotFoundException;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    public static void test() {

        JarFile jar = null;
        try {
            URL url = new URL("file:c:\\Users\\SFI\\Projects\\ooka\\BuchungsSystem\\target\\BuchungsSystem-1.0.jar");
//            URL[] urls = new URL[]{url};
//            URLClassLoader loader = new URLClassLoader(urls);
//            Users\\SFI\\Projects\\ooka\\BuchungsClient\\target\\BuchungsClient-1.0.jar!/"); //\\org\\ooka\\sfisc12s\\buchung\\system\\entity\\");
//            URL url = new URL("jar", "C:\\lib\\BuchungsSystem-1.0.jar",
                    //jar:file://c:\\Users\\SFI\\Projects\\ooka\\BuchungsClient\\target\\BuchungsClient-1.0.jar!/"); //\\org\\ooka\\sfisc12s\\buchung\\system\\entity\\");
            ExtendedClassLoader loader = new ExtendedClassLoader();
            loader.addUrl(url);

//            Class<?> clazz = loader.loadClass("org.ooka.sfisc12s.buchung.system.entity.Hotel");
//            System.out.println(clazz.getSimpleName());
//            Object o = clazz.newInstance();
            jar = new JarFile(url.getFile());
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                // valide Klassen
                if (!entry.getName().endsWith(".class"))
                    continue;

                // Klassenpfad normalisieren und in classloader laden
                Class<?> clazz = loader.loadClass(entry.getName().replaceAll("/", ".").replaceAll(".class", ""));

                if(clazz.getSimpleName().equals("HotelRetrievalProxy")) {
                    Object o = clazz.newInstance();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        test();
        RuntimeEnvironment re = RuntimeEnvironment.getInstance();
        CommandStation comm = new CommandStation(System.getProperty("user.dir") + "/config.txt");
        comm.addCommand(new CommandExit("(quit)|(exit)"))
                .addCommand(new CommandLoadClass("load class"))
                .addCommand(new CommandLoadJar("load jar"))
                .addCommand(new CommandLoadPath("load path"))
                .addCommand(new CommandUnload("unload"))
                .addCommand(new CommandStart("start"))
                .addCommand(new CommandStop("stop"))
                .addCommand(new CommandGetStatus("get status"))
                .addCommand(new CommandRemove("remove"))
                .addCommand(new CommandLoadConfig("load config", comm));

        new Terminal(comm).startTerminal();
    }
}
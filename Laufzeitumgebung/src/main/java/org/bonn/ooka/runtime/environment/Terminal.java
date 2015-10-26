package org.bonn.ooka.runtime.environment;

import javafx.util.Pair;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.PatternMap;
import org.bonn.ooka.runtime.util.command.CommandExit;
import org.bonn.ooka.runtime.util.exception.CommandNotFoundException;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.state.annotation.StartMethod;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

public class Terminal {

    private PatternMap<Command> commandMap = new PatternMap<>();

    public Terminal() {
    }

    public PatternMap<Command> getCommandMap() {
        return commandMap;
    }

    public Terminal addCommand(Command<?> command) {
        commandMap.putIfAbsent(command.getName(), new Pair(command.getArgs(), command));

//        this.addCompleter((buf, cursor, candidates) -> {
//            if (buf.equals(commandPattern) || (buf.length() <= commandPattern.length()) && buf.equals(commandPattern.substring(0, buf.length())))
//                candidates.add(commandPattern);
//
//            return candidates.isEmpty() ? -1 : 0;
//        });
        return this;
    }

    public Terminal removeCommand(Command command) {
        commandMap.computeIfPresent(command.getName(), (cmd, action) -> commandMap.remove(cmd));
        return this;
    }

    @StartMethod
    public void start() throws CommandNotFoundException {
        Scanner scan = new Scanner(System.in);
        String line;

        while (true) {
            System.out.print("> ");

            if ((line = scan.nextLine()) == null)
                break;

            Pair<String, Command> match = commandMap.getMatchingPair(line);

            if (match != null)
                match.getValue().getMethod().accept(match.getKey().trim());
            else throw new CommandNotFoundException(String.format("Command '%s' not found", line));
        }
    }

    public static void main(String[] args) {

//        String s = "file://c:/Projects/ooka/BuchungsClient/target/classes/org/bonn/ooka/buchung/client/service/";
//        String s = "file://c:/Projects/ooka/Laufzeitumgebung/src/main/resources/TestClass.class";
//        String url2 = "file://c:/Projects/ooka/Laufzeitumgebung/src/main/resources/Import/";
//        int separator = s.lastIndexOf('/') + 1;
//        String url = s.substring(0, separator);
//        String file = s.substring(separator).replaceAll("(\\..*)", "");
//
//        try {
//            ExtendedClassLoader loader = new ExtendedClassLoader();
//            loader.addUrl(new URL(url));
//            loader.addUrl(new URL(url2));
////            Class<?> loadedClass = loader.loadClass("TestImport");
//            Class<?> loadedClass = loader.loadClass(file);
////            Class<?> loadedClass = loader.loadClass("org.bonn.ooka.buchung.client.service.LocalCaching");
////            Class<?> loadedClass = Class.forName("org.bonn.ooka.buchung.client.service.LocalCaching", false, loader);
//            System.out.println(loadedClass.getClassLoader());
//            loadedClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Terminal terminal = new Terminal();
        RuntimeEnvironment re = RuntimeEnvironment.getInstance();

        try {
            terminal
                    .addCommand(new CommandExit("(quit)|(exit)"))
                    .addCommand(re.loadClass)
                    .addCommand(re.loadJar)
                    .addCommand(re.startClass)
                    .addCommand(re.stopClass)
                    .start();
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
            System.out.println("Allowed commands:");
            terminal.getCommandMap().keySet().stream().forEachOrdered(System.out::println);
        }
    }

    public static String COMMA_SPLIT = "\\h*,(?=([^\"]*\"[^\"]*\")*[^\"]*$)\\h*";

    public static String WORD_BASE = "\\w\\_\\-\\:\\(\\)\\.\\/\\\\öäüÖÄÜ";

    public static String EXT(String extension) {
        if (extension == null || extension.isEmpty())
            return "";

        return String.format("(\\.%s)", extension);
    }

    public static String WORD(String ext) {
        return String.format("[%s]+%s", WORD_BASE, EXT(ext));
    }

    public static String QUOTED_WORD(String ext) {
        return String.format("\"[%s\\s]+%s\"", WORD_BASE, EXT(ext));
    }

    public static String WORD_OR_QUOTED(String ext) {
        return String.format("(%s|%s)", WORD(ext), QUOTED_WORD(ext));
    }

    public static String MODIFIED_ARGS(String ext) {
        return String.format("(\\s+%s(,\\s*%s)*)?", WORD_OR_QUOTED(ext), WORD_OR_QUOTED(ext));
    }

    public static String DEFAULT_ARGS = MODIFIED_ARGS("");
}
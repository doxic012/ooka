package org.bonn.ooka.runtime.util.command;

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
    public static String COMMA_SPLIT = "\\h*,(?=([^\"]*\"[^\"]*\")*[^\"]*$)\\h*";

    public static String WORD_BASE(String base) {
        return String.format("\\w\\_\\-\\:\\(\\$\\)\\.\\/\\\\%s", base); //\00FC\00E4\00F6\00C4\00D6\00DC
    }

    public static String EXT(String extension) {
        if (extension == null || extension.isEmpty())
            return "";

        return String.format("(%s)", extension);
    }

    public static String WORD(String base, String ext) {
        return String.format("[%s]+%s", WORD_BASE(base), EXT(ext));
    }

    public static String QUOTED_WORD(String base, String ext) {
        return String.format("\"[%s\\s]+%s\"", WORD_BASE(base), EXT(ext));
    }

    public static String WORD_OR_QUOTED(String base, String ext) {
        return String.format("(%s|%s)", WORD(base, ext), QUOTED_WORD(base, ext));
    }

    public static String MODIFIED_ARGS(String base, String ext) {
        return String.format("(\\s+%s(,\\s*%s)*)?", WORD_OR_QUOTED(base, ext), WORD_OR_QUOTED(base, ext));
    }

    public static String DEFAULT_ARGS = MODIFIED_ARGS("", "");

    public abstract Consumer<T> getMethod();

    public abstract String getName();

    public abstract String getArgs();

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
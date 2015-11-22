package org.ooka.sfisc12s.util.SQL;

import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 22.11.2015.
 */
public class QueryManager {
    /**
     * Generate filter query by array of params, with a length dividable by 2
     * "a" = b, "c" = d, ...
     *
     * @param delimiter The delimiter between each
     * @return
     */
    public static String getKeyValueArgs(String delimiter, Object... args) {
        return getKeyValueArgs("", delimiter, args);
    }

    public static String getKeyValueArgs(String prefix, String delimiter, Object... args) {
        String kvArgs = "";

        if (args == null || args.length <= 0)
            return "";

        int length = args.length - args.length % 2;
        List<String> argList = new ArrayList<>();
        for (int i = 0; i < length; i += 2)
            argList.add(String.format(" \"%s\"='%s' ", args[i], args[i + 1]));

        return prefix + String.join(delimiter, argList);
    }
}

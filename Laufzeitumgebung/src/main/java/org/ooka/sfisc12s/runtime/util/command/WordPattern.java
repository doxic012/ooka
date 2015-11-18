package org.ooka.sfisc12s.runtime.util.command;

/**
 * Created by steve on 09.11.15.
 */
public final class WordPattern {
    public static String SPLIT(String delimiter) {
        return String.format("\\h*%s(?=([^\"]*\"[^\"]*\")*[^\"]*$)\\h*", delimiter);
    }

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
        return String.format("\"[%s\\s\\,]+%s\"", WORD_BASE(base), EXT(ext));
    }

    public static String WORD_OR_QUOTED(String base, String ext) {
        return String.format("(%s|%s)", WORD(base, ext), QUOTED_WORD(base, ext));
    }

    public static String MODIFIED_ARGS(String base, String ext, String delimiter) {
        return String.format("(\\s+%s(%s\\s*%s)*)?", WORD_OR_QUOTED(base, ext), delimiter, WORD_OR_QUOTED(base, ext));
    }

    public static String MODIFIED_ARGS(String base, String ext) {
        return MODIFIED_ARGS(base, ext, ",");
    }

    public static String DEFAULT_ARGS = MODIFIED_ARGS("", "");
}

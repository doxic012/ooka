package org.ooka.sfisc12s.runtime.util;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMap<T> extends HashMap<String, Pair<String, T>> {

    public PatternMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public PatternMap(int initialCapacity) {
        super(initialCapacity);
    }

    public PatternMap() {
    }

    public PatternMap(Map<? extends String, ? extends Pair<String, T>> m) {
        super(m);
    }

    public T getMatch(String input) {
        return getMatchingPair(input).getValue();
    }

    public String getArgs(String input) {
        return getMatchingPair(input).getKey();
    }

    public Pair<String, T> getMatchingPair(String input) {
        for (String s : keySet()) {
            Pattern p = Pattern.compile(s + get(s).getKey());

            if (p.matcher(input).matches())
                return new Pair<>(input.replaceFirst(s, ""), get(s).getValue());
        }

        return null;
    }

    public T getMatchingCommand(String input) {
        for (String s : keySet()) {
            Pattern p = Pattern.compile(s);

            if (p.matcher(input).matches())
                return get(s).getValue();
        }

        return null;
    }
}

package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.component.ClassComponent;
import org.bonn.ooka.runtime.util.exception.StateMethodException;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadClass extends Command<String> {

    private String args = DEFAULT_ARGS;

    private String name;

    private Map<String, ClassComponent> componentMap;

    private ExtendedClassLoader classLoader;

    public CommandLoadClass(String name, Map<String, ClassComponent> componentMap, ExtendedClassLoader classLoader) {
        this.name = name;
        this.componentMap = componentMap;
        this.classLoader = classLoader;
    }

    @Override
    public Consumer<String> getMethod() {
        return (className) -> {
            // verify arguments
            if ((className = verifyArguments(className)).isEmpty())
                return;

            // split by comma outside of quotes
            for (String classUrl : className.split(COMMA_SPLIT)) {
                int separator = classUrl.lastIndexOf('/') + 1;
                String url = classUrl.substring(0, separator);
                String file = classUrl.substring(separator).replaceAll("(\\..*)", "");

                try {
                    // try to load each component in the URL
                    componentMap.compute(file, (n, c) -> c == null ? new ClassComponent(n, url, classLoader) : c).load();
                } catch (StateMethodException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getArgs() {
        return args;
    }
}

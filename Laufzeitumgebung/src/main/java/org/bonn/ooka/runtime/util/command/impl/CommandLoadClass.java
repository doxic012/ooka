package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.environment.component.impl.ClassComponent;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;
import org.bonn.ooka.runtime.environment.loader.ExtendedClassLoader;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadClass extends Command<String> {

    private String args = MODIFIED_ARGS("", "\\.class");

    private String name;

    private Map<String, Component> componentMap;

    private ExtendedClassLoader classLoader;

    public CommandLoadClass(String name, Map<String, Component> componentMap, ExtendedClassLoader classLoader) {
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
            for (String classUrl : className.split(SPLIT(","))) {
                int separator = classUrl.lastIndexOf('/') + 1;
                String file = classUrl.substring(separator).replaceAll(".class", "");
                String path = classUrl.substring(0, separator);

                try {
                    URL url = new URL("file://" + path);
                    componentMap.compute(file, (name, c) -> c == null ? new ClassComponent(url, name, classLoader) : c).load();
                } catch (StateException | MalformedURLException e) {
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

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "path/to/class[.class][, ...]",
                "Load one or more classes into the runtime environment. All included paths will be added as classpaths.\n");
    }
}

package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.util.command.Command;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandGetStatus extends Command<String> {

    private String args = DEFAULT_ARGS;

    private String name;

    private Map<String, Component> componentMap;

    public CommandGetStatus(String name, Map<String, Component> componentMap) {
        this.name = name;
        this.componentMap = componentMap;
    }

    @Override
    public Consumer<String> getMethod() {
        return (className) -> {

            // verify arguments
            if (className.isEmpty()) {
                componentMap.forEach((n, c) -> System.out.printf("%s - %s%s", n, c.getStatus(), System.lineSeparator()));
                return;
            }

            // split by comma outside of quotes
            for (String classUrl : className.split(SPLIT(","))) {
                int separator = classUrl.lastIndexOf('/') + 1;
                String file = classUrl.substring(separator).replaceAll("(\\..*)", "");

                componentMap.compute(file, (n, c) -> {
                    if (c == null)
                        System.out.printf("Component or class '%s' does not exist%s", n, System.lineSeparator());
                    else
                        System.out.println(c.getStatus());
                    return c;
                });
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
        return String.format("%s %s:\t%s",
                getName(),
                "ClassComponent",
                "Displays the status of an already loaded component. The component needs to be loaded first via the command \"load class\".\n");
    }
}

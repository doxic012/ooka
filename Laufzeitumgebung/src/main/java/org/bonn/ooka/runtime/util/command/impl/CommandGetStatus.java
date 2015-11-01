package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.component.ClassComponent;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandGetStatus extends Command<String> {

    private String args = DEFAULT_ARGS;

    private String name;

    private Map<String, ClassComponent> componentMap;

    public CommandGetStatus(String name, Map<String, ClassComponent> componentMap) {
        this.name = name;
        this.componentMap = componentMap;
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

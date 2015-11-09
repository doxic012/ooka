package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStart extends Command<String> {

    private String args = MODIFIED_ARGS(" ", "");

    private String name;

    private Map<String, Component> componentMap;

    public CommandStart(String name, Map<String, Component> componentMap) {
        this.name = name;
        this.componentMap = componentMap;
    }

    @Override
    public Consumer<String> getMethod() {
        return (arguments) -> {
            // verify arguments
            if ((arguments = verifyArguments(arguments)).isEmpty())
                return;

            // split by comma outside of quotes
            for (String startClass : arguments.split(SPLIT(","))) {
                int separator = startClass.indexOf(" ");
                String component = separator != -1 ? startClass.substring(0, separator) : startClass;
                String startArgs[] = startClass.substring(separator + 1).split(SPLIT(" "));

                componentMap.compute(component, (n, c) -> {
                    try {
                        if (c == null)
                            System.out.printf("Component or class '%s' does not exist%s", n, System.lineSeparator());
                        else
                            c.start(startArgs);
                    } catch (StateException e) {
                        e.printStackTrace();
                    }
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
        return String.format("'%s' %s:\t%s",
                getName(),
                "Component1[, Component2, ...]",
                "Start one or more already loaded classes/components from the runtime environment. If one or more of the specified components are already started, they will be ignored.\n");
    }
}

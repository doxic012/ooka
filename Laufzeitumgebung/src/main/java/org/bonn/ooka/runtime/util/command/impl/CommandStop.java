package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStop extends Command<String> {

    private String args = DEFAULT_ARGS;

    private String name;

    private Map<String, Component> componentMap;

    public CommandStop(String name, Map<String, Component> componentMap) {
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
            for (String arg : arguments.split(SPLIT(" "))) {
                int separator = arg.lastIndexOf('/') + 1;
                String component = arg.substring(separator).replaceAll("(\\..*)", "");

                componentMap.compute(component, (n, c) -> {

                    try {
                        if (c == null)
                            System.out.printf("Component or class '%s' does not exist%s", n, System.lineSeparator());
                        else
                            c.stop();
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
                "Stop one or more already started classes/components from the runtime environment.\n" +
                        "If one or more of the specified components are already stopped, they will be ignored.\n");
    }
}

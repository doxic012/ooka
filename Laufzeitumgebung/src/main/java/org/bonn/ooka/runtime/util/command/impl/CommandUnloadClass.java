package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.component.ClassComponent;
import org.bonn.ooka.runtime.util.exception.StateMethodException;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandUnloadClass extends Command<String> {

    private String args = DEFAULT_ARGS;

    private String name;

    private Map<String, ClassComponent> componentMap;

    public CommandUnloadClass(String name, Map<String, ClassComponent> componentMap) {
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
            for (String arg : arguments.split(COMMA_SPLIT)) {
                int separator = arg.lastIndexOf('/') + 1;
                String component = arg.substring(separator).replaceAll("(\\..*)", "");

                componentMap.compute(component, (n, c) -> {
                    try {
                        if (c == null)
                            System.out.printf("Component or class '%s' does not exist%s", n, System.lineSeparator());
                        else
                            c.unload();
                    } catch (StateMethodException e) {
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
}

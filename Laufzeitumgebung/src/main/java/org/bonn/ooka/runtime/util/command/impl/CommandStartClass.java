package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.component.ClassComponent;
import org.bonn.ooka.runtime.util.state.exception.StateMethodException;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStartClass extends Command<String> {

    private String args = MODIFIED_ARGS("\\s", "");

    private String name;

    private Map<String, ClassComponent> componentMap;

    public CommandStartClass(String name, Map<String, ClassComponent> componentMap) {
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
            for (String startClass : arguments.split(COMMA_SPLIT)) {
                int separator = startClass.indexOf(' ');
                String component = (separator != -1 ? startClass.substring(0, separator) : startClass).replaceAll("(\\..*)", "");
                String startArgs[] = separator != -1 ? startClass.substring(separator + 1).split(" ") : null;

                componentMap.compute(component, (n, c) -> {
                    try {
                        if (c == null)
                            System.out.printf("Component or class '%s' does not exist%s", n, System.lineSeparator());
                        else
                            c.start(startArgs);
                    } catch (StateMethodException e) {
                        e.printStackTrace();
                    }
                    return c;
                });
            }
        }
                ;
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
                "ClassComponent1[, ClassComponent2, ...]",
                "Start one or more already loaded classes/components from the runtime environment. If one or more of the specified components are already started, they will be ignored.\n");
    }
}

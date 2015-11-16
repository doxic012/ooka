package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;

import java.util.function.Consumer;

import static org.bonn.ooka.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandUnloadClass extends Command<String> {

    public CommandUnloadClass(String name) {
        super(name, DEFAULT_ARGS);
    }

    @Override
    public Consumer<String> getMethod() {
        return (arguments) -> {

            // verify arguments
            if ((arguments = verifyArguments(arguments)).isEmpty())
                return;

            // split by comma outside of quotes
            for (String arg : arguments.split(SPLIT(","))) {
                int separator = arg.lastIndexOf('/') + 1;
                String component = arg.substring(separator).replaceAll("(\\..*)", "");

                RuntimeEnvironment
                        .getInstance()
                        .getComponents()
                        .compute(component, (n, c) -> {
                            try {
                                if (c == null)
                                    getLogger().debug("Component or class '%s' does not exist%s", n);
                                else
                                    c.unload();
                            } catch (StateException e) {
                                getLogger().error(e, "");
                            }

                            return null;
                        });
            }
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "ClassComponent1[, ClassComponent2, ...]",
                "Unload one or more already loaded classes/components from the runtime environment.\n");
    }
}

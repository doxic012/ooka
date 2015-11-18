package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.command.Command;

import java.util.function.Consumer;
import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStop extends Command<String> {

    public CommandStop(String name) {
        super(name, MODIFIED_ARGS(" ", ""));
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
                String component = arg.substring(separator);

                System.out.println(arg);
                RuntimeEnvironment
                        .getInstance()
                        .getComponents()
                        .compute(component, (n, c) -> {
                            try {
                                if (c == null)
                                    getLogger().debug("Component or class '%s' does not exist%s", n, System.lineSeparator());
                                else
                                    c.stop();
                            } catch (StateException e) {
                                getLogger().error(e);
                            }
                            return c;
                        });
            }
        };
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

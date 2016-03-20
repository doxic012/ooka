package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;

import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStop extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandStop.class);

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
                Component c = RuntimeEnvironment.getInstance().get(Integer.parseInt(component));

                try {

                    if (c == null)
                        log.debug("Component or class '%s' does not exist.", component);
                    else
                        c.stop();
                } catch (StateException | ScopeException e) {
                    log.error(e, "Error while stopping component '%s'", c);
                }
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

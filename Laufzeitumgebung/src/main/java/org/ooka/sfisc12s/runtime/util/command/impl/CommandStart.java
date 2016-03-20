package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;

import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandStart extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandStart.class);

    public CommandStart(String name) {
        super(name, MODIFIED_ARGS(" ", ""));
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
                String startArgs[] = separator != -1 ? startClass.substring(separator + 1).split(SPLIT(" ")) : null;
                Component c = RuntimeEnvironment.getInstance().get(Integer.parseInt(component));

                try {
                    if (c == null)
                        log.debug("Component or class '%s' does not exist.", component);
                    else
                        c.start(startArgs);
                } catch (StateException | ScopeException e) {
                    log.error(e, "Error while starting component '%s'", c);
                }
            }
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "Component1[, Component2, ...]",
                "Start one or more already loaded classes/components from the runtime environment. If one or more of the specified components are already started, they will be ignored.\n");
    }
}

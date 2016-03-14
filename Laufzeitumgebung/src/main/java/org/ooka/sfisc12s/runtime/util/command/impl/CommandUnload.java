package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;

import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandUnload extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandUnload.class);

    public CommandUnload(String name) {
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
                String name = arg.substring(separator).replaceAll("(\\..*)", "");
                ComponentBase c = RuntimeEnvironment.getInstance().get(Integer.parseInt(name));

                try {
                    if (c == null)
                        log.debug("Component '%s' does not exist.", name);
                    else
                        c.unload();
                } catch (StateException | ScopeException e) {
                    log.error(e, "Error while unloading component '%s'", c);
                }
            }
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "ClassComponent1[, ClassComponent2, ...]",
                "Unload one or more already loaded components from the runtime environment.\n");
    }
}

package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.persistence.ComponentBase;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;

import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.DEFAULT_ARGS;
import static org.ooka.sfisc12s.runtime.util.command.WordPattern.SPLIT;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandRemove extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandRemove.class);

    public CommandRemove(String name) {
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
                ComponentBase c = RuntimeEnvironment.getInstance().get(Integer.parseInt(component));

                if (c == null)
                    log.debug("Component '%s' does not exist.", component);
                else if (!c.isUnloaded()) {
                    log.debug("Component '%s' needs to be unloaded first", component);
                } else {
                    log.debug("Component '%s' removed: %s", component, RuntimeEnvironment.getInstance().remove(c));
                }
            }
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "ClassComponent1[, ClassComponent2, ...]",
                "Remove one or more already unloaded components from the runtime environment.\n");
    }
}

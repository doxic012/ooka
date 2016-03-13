package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable.Scope;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.MODIFIED_ARGS;
import static org.ooka.sfisc12s.runtime.util.command.WordPattern.SPLIT;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandSetScope extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandSetScope.class);

    public CommandSetScope(String name) {
        super(name, MODIFIED_ARGS(" ", ""));
    }

    @Override
    public Consumer<String> getMethod() {
        return (arguments) -> {
            // verify arguments
            if ((arguments = verifyArguments(arguments)).isEmpty())
                return;

            RuntimeEnvironment re = RuntimeEnvironment.getInstance();
            re.setScope(Scope.valueOf(arguments));
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s%s.\n",
                getName(),
                "ScopeName",
                "Set the current scope of the runtime environment. Available Scopes:", Arrays.toString(Scope.values()));
    }
}

package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;

import java.util.function.Consumer;
import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandGetStatus extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandGetStatus.class);

    public CommandGetStatus(String name) {
        super(name, DEFAULT_ARGS);
    }

    @Override
    public Consumer<String> getMethod() {
        return (className) -> {

            // verify arguments
            if (className.isEmpty()) {
                RuntimeEnvironment
                        .getInstance()
                        .getComponentMap()
                        .forEach((n, c) -> log.debug(c.getStatus()));
                return;
            }

            // split by comma outside of quotes
            for (String classUrl : className.split(SPLIT(","))) {
                int separator = classUrl.lastIndexOf('/') + 1;
                String file = classUrl.substring(separator).replaceAll("(\\..*)", "");

                RuntimeEnvironment
                        .getInstance()
                        .getComponentMap()
                        .compute(file, (n, c) -> {
                            if (c == null)
                                log.debug("Component or class '%s' does not exist%s", n, System.lineSeparator());
                            else
                                log.debug(c.getStatus());
                            return c;
                        });
            }
        };
    }


    @Override
    public String getCommandDescription() {
        return String.format("%s %s:\t%s",
                getName(),
                "ClassComponent",
                "Displays the status of an already loaded component. The component needs to be loaded first via the command \"load class\".\n");
    }
}

package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.command.Command;

import java.util.function.Consumer;
import static org.bonn.ooka.runtime.util.command.WordPattern.*;
/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandGetStatus extends Command<String> {

    public CommandGetStatus(String name, RuntimeEnvironment re) {
        super(name, DEFAULT_ARGS, re);
    }

    @Override
    public Consumer<String> getMethod() {
        return (className) -> {


            // verify arguments
            if (className.isEmpty()) {
                getRE().getComponents().forEach((n, c) -> System.out.printf("%s - %s%s", n, c.getStatus(), System.lineSeparator()));
                return;
            }

            // split by comma outside of quotes
            for (String classUrl : className.split(SPLIT(","))) {
                int separator = classUrl.lastIndexOf('/') + 1;
                String file = classUrl.substring(separator).replaceAll("(\\..*)", "");

                getRE().getComponents().compute(file, (n, c) -> {
                    if (c == null)
                        getLogger().debug("Component or class '%s' does not exist%s", n, System.lineSeparator());
                    else
                        System.out.println(c.getStatus());
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

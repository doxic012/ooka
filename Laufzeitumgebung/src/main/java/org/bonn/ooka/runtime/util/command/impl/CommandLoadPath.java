package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.command.Command;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;

import static org.bonn.ooka.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadPath extends Command<String> {

    public CommandLoadPath(String name, RuntimeEnvironment re) {
        super(name, DEFAULT_ARGS, re);
    }

    @Override
    public Consumer<String> getMethod() {
        return (className) -> {

            // verify arguments
            if ((className = verifyArguments(className)).isEmpty())
                return;

            // split by comma outside of quotes
            for (String classUrl : className.split(SPLIT(","))) {
//                int separator = classUrl.lastIndexOf('/') + 1;
//                String url = classUrl.substring(0, separator);

                try {
                    getRE().getClassLoader().addUrl(new URL("file://" + classUrl));
                } catch (URISyntaxException | MalformedURLException e) {
                    getLogger().error(e);
                }
            }
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "path/to/class/ [, .../]",
                "Load one or more paths of resources or components into the runtime environment. Already loaded paths will be ignored.\n");
    }
}

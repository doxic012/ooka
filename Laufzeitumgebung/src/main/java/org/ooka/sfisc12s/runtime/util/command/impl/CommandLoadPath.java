package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;
import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadPath extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandLoadPath.class);

    public CommandLoadPath(String name) {
        super(name, DEFAULT_ARGS);
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
                    RuntimeEnvironment
                            .getInstance()
                            .getClassLoader().addUrl(new URL("file://" + classUrl));
                } catch (URISyntaxException | MalformedURLException e) {
                    log.error(e);
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

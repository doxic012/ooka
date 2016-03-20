package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.persistence.impl.ReferenceComponent;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

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
                int separator = classUrl.lastIndexOf('/') + 1;
                String file = classUrl.substring(separator).replaceAll(".jar", "");

                try {
                    RuntimeEnvironment re = RuntimeEnvironment.getInstance();
                    Component c = re.getOrAdd(new ReferenceComponent(file, new URL("file:" + classUrl), re.getScope()));

                    if (c == null)
                        log.debug("Error while adding component or class '%s'. Invalid file.", file);
                    else
                        c.load();
                } catch (IOException | StateException e) {
                    log.error(e, "Error while loading reference component");
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

package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.environment.component.impl.JarComponent;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static org.bonn.ooka.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadJar extends Command<String> {


    public CommandLoadJar(String name) {
        super(name, MODIFIED_ARGS("", "\\.jar"));
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
                    URL url = new URL("file://" + classUrl);
                    RuntimeEnvironment
                            .getInstance()
                            .getComponents()
                            .compute(file, (name, c) -> c == null ? new JarComponent(url, name) : c).load();
                } catch (StateException | MalformedURLException e) {
                    getLogger().error(e);
                }
            }
        };
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "path/to/class[.class][, ...]",
                "Load one or more classes into the runtime environment. All included paths will be added as classpaths.\n");
    }
}

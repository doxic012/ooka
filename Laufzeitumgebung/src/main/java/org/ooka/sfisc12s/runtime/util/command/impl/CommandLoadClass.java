package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.component.impl.ClassComponent;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;
/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadClass extends Command<String> {

    public CommandLoadClass(String name) {
        super(name, MODIFIED_ARGS("", "\\.class"));
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
                String file = classUrl.substring(separator).replaceAll(".class", "");
                String path = classUrl.substring(0, separator);

                try {
                    URL url = new URL("file://" + path);
                    RuntimeEnvironment
                            .getInstance()
                            .getComponents()
                            .compute(file, (name, c) -> c == null ? new ClassComponent(url, name) : c).load();
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

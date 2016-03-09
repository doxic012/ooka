package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.component.impl.ClassComponent;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;


public class CommandLoadClass extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandLoadClass.class);

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
                String filePath = classUrl.substring(separator);
                String file = filePath.replaceAll(".class", "");
                String path = classUrl.substring(0, separator);
                String name = verifyArguments("", "Enter name for jar-file:");
                name = name.isEmpty() ? file : name;

                try {
                    URL url = new URL("file://" + path);
                    RuntimeEnvironment.getInstance().
                            getOrAdd(new ClassComponent(name, url, "no scope yet")).
                            load();
                    //TODO: if/else
                } catch (IOException e) {
                    log.error(e, "Error while loading class-file '%s'", file);
                } catch (StateException e) {
                    log.error(e, "Error while loading ClassComponent '%s'", file);
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

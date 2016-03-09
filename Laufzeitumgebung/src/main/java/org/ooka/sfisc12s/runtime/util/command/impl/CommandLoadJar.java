package org.ooka.sfisc12s.runtime.util.command.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.impl.JarComponent;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.command.Command;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

import static org.ooka.sfisc12s.runtime.util.command.WordPattern.*;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadJar extends Command<String> {

    private static Logger log = LoggerFactory.getRuntimeLogger(CommandLoadJar.class);

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
                String filePath = classUrl.substring(separator);
                String file = filePath.replaceAll(".jar", "");
                String name = verifyArguments("", "Enter name for jar-file:");

                try {
                    URL url = new URL("file://" + classUrl);
                    RuntimeEnvironment.getInstance().
                            getOrAdd(new JarComponent(name.isEmpty() ? file : name, url, "no scope yet")).
                            load();
                    //TODO: if/else?
                } catch (NullPointerException | IOException e) {
                    log.error(e, "Error while loading jar-file '%s'", file);
                } catch (StateException e) {
                    log.error(e, "Error while loading JarComponent '%s'", file);
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

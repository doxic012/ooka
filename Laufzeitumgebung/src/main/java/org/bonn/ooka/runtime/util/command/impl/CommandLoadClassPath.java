package org.bonn.ooka.runtime.util.command.impl;

import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Created by Stefan on 26.10.2015.
 */
public class CommandLoadClassPath extends Command<String> {

    private String args = MODIFIED_ARGS("", "(\\/|\\\\)");

    private String name;

    private ExtendedClassLoader classLoader;

    public CommandLoadClassPath(String classPath, ExtendedClassLoader classLoader) {
        this.name = classPath;
        this.classLoader = classLoader;
    }

    @Override
    public Consumer<String> getMethod() {
        return (className) -> {

            // verify arguments
            if ((className = verifyArguments(className)).isEmpty())
                return;

            // split by comma outside of quotes
            for (String classUrl : className.split(COMMA_SPLIT)) {
                int separator = classUrl.lastIndexOf('/') + 1;
                String url = classUrl.substring(0, separator);

                try {
                    classLoader.addUrl(new URL("file://"+url));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getArgs() {
        return args;
    }

    @Override
    public String getCommandDescription() {
        return String.format("'%s' %s:\t%s",
                getName(),
                "path/to/class/ [, .../]",
                "Load one or more paths of resources or components into the runtime environment. Already loaded paths will be ignored.\n");
    }
}

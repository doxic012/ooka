package org.bonn.ooka.runtime.environment;

import org.bonn.ooka.runtime.util.component.ClassComponent;
import org.bonn.ooka.runtime.util.exception.StateMethodNotAllowedException;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.command.Command;
import org.bonn.ooka.runtime.util.state.StateLoaded;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class RuntimeEnvironment {


    private static RuntimeEnvironment instance = null;

    public static RuntimeEnvironment getInstance() {
        if (instance == null)
            instance = new RuntimeEnvironment();

        return instance;
    }

    private RuntimeEnvironment() {
    }

    private HashMap<String, ClassComponent> componentMap = new HashMap<>();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    public Command loadClass = new Command<String>("load class", Terminal.MODIFIED_ARGS("class"), (classPath) -> {
        if (classPath == null || classPath.length() == 0) {
            System.out.printf("Enter path to class(es):%s> ", System.lineSeparator());

            Scanner scan = new Scanner(System.in);
            classPath = scan.nextLine();
        }

        // invalid path to class(es)
        if (classPath == null || classPath.isEmpty())
            return;

        // replace all backslash with slash
        classPath = classPath.replaceAll("\\\\", "/");
        try {
            // split by comma outside of quotes
            for (String classUrl : classPath.split(Terminal.COMMA_SPLIT)) {
                int separator = classUrl.lastIndexOf('/') + 1;
                String url = classUrl.substring(0, separator);
                String file = classUrl.substring(separator).replaceAll("(\\..*)", "");

                // add url to loaded paths
                classLoader.addUrl(new URL("file://" + url));

                if(componentMap.containsKey(file))
                    System.out.printf("Class already loaded: %s%s", file, System.lineSeparator());

                // load each component in the URL
                componentMap.computeIfAbsent(file, (name) -> {
                    try {
                        Class<?> loadedClass = classLoader.loadClass(name);
                        System.out.printf("Class loaded: %s%s", name, System.lineSeparator());

                        ClassComponent component = new ClassComponent(String.valueOf(loadedClass.getClass().hashCode()), name, loadedClass);
                        component.setState(new StateLoaded(component));

                        return component;
                    } catch (ClassNotFoundException ex) {
                        System.err.printf("Could not load class: %s%s", name, System.lineSeparator());
                        ex.printStackTrace();
                    }
                    return null;
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

    public Command startClass = new Command<String>("start class", Terminal.DEFAULT_ARGS, (className) -> {
        if(componentMap.containsKey(className)) {
            ClassComponent component = componentMap.get(className);

            try {
                component.getState().start();
                System.out.println("Component started");
            } catch (StateMethodNotAllowedException e) {
                e.printStackTrace();
            }
        }
    });
    public Command stopClass = new Command<String>("stop class", Terminal.DEFAULT_ARGS, (className) -> {

    });
    public Command loadJar = new Command<String>("load jar", Terminal.MODIFIED_ARGS("jar"), (classPath) -> {

    });

}


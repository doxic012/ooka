package org.bonn.ooka.runtime.environment;

import org.bonn.ooka.runtime.util.component.ClassComponent;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;

import java.util.HashMap;

public class RuntimeEnvironment {


    private static RuntimeEnvironment instance = null;

    public static RuntimeEnvironment getInstance() {
        if (instance == null)
            instance = new RuntimeEnvironment();

        return instance;
    }

    private RuntimeEnvironment() {
    }

    private HashMap<String, ClassComponent> components = new HashMap<>();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    public HashMap<String, ClassComponent> getComponents() {
        return components;
    }

    //    public Command loadJar = new Command<String>("load jar", Terminal.MODIFIED_ARGS("jar"), (classPath) -> {
//
//    });

}


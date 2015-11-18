package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.RuntimeLogger;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.util.HashMap;

public class RuntimeEnvironment {

    private Logger log = new RuntimeLogger();

    private HashMap<String, Component> components = new HashMap<>();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private static RuntimeEnvironment instance = null;

    public static RuntimeEnvironment getInstance() {
        if (instance == null)
            instance = new RuntimeEnvironment();

        return instance;
    }

    private RuntimeEnvironment() {
    }

    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    public HashMap<String, Component> getComponents() {
        return components;
    }

    public Logger getLogger() {
        return log;
    }
}
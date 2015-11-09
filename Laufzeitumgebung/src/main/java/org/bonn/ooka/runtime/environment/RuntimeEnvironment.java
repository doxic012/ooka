package org.bonn.ooka.runtime.environment;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.loader.ExtendedClassLoader;

import java.util.HashMap;

public class RuntimeEnvironment {

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
}
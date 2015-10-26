package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.exception.StateMethodException;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;

import java.lang.reflect.Modifier;
import java.net.URL;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateUnloaded implements State {

    private Component component;
    private ExtendedClassLoader classLoader;

    public StateUnloaded(Component component, ExtendedClassLoader classLoader) {
        this.component = component;
        this.classLoader = classLoader;
    }

    @Override
    public void start() throws StateMethodException {
        throw new StateMethodException("Component is not loaded.");
    }

    @Override
    public void stop() throws StateMethodException {
        throw new StateMethodException("Component is not loaded");
    }

    @Override
    public void load() {
        try {
            classLoader.addUrl(new URL("file://" + component.getPath()));
            Class<?> loadedClass = classLoader.loadClass(component.getName());
            component.setId(String.valueOf(loadedClass.hashCode()));
            component.setComponentClass(loadedClass);

            // instantiate only when possible
//            int mod = loadedClass.getModifiers();
//            if(!(Modifier.isAbstract(mod) || Modifier.isInterface(mod) || Modifier.isFinal(mod)));
//            component.setInstance(loadedClass.newInstance());
            component.setState(new StateStopped(component));

            System.out.printf("Component loaded: %s%s", component.getName(), System.lineSeparator());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("Could not load component: %s%s", component.getName(), System.lineSeparator());
        }
    }

    @Override
    public void unload() throws StateMethodException {
        throw new StateMethodException("Component is already unloaded.");
    }
}

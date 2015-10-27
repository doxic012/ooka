package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.exception.StateMethodException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateUnloaded implements State {

    private Component component;
    private ClassLoader classLoader;

    public StateUnloaded(Component component, ClassLoader classLoader) {
        this.component = component;
        this.classLoader = classLoader;
    }

    @Override
    public void start(Object... args) throws StateMethodException {
        throw new StateMethodException("Component is not loaded.");
    }

    @Override
    public void stop() throws StateMethodException {
        throw new StateMethodException("Component is not loaded");
    }

    @Override
    public void load() {
        try {
            Class<?> loadedClass = classLoader.loadClass(component.getName());
            component.setId(String.valueOf(loadedClass.hashCode()));
            component.setComponentClass(loadedClass);
            component.setState(new StateStopped(component));

            System.out.printf("Component loaded: %s%s", component.getName(), System.lineSeparator());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.printf("Could not load component: %s%s", component.getName(), System.lineSeparator());
        } catch(NoClassDefFoundError e) {
            e.printStackTrace();
            System.err.printf("Component missing.%s", component.getName(), System.lineSeparator());
        }
    }

    @Override
    public void unload() throws StateMethodException {
        throw new StateMethodException("Component is already unloaded.");
    }
}

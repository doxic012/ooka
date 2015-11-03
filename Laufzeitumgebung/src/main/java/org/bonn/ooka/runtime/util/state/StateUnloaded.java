package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.Logger.Impl.SystemLogger;
import org.bonn.ooka.runtime.util.Logger.Logger;
import org.bonn.ooka.runtime.util.annotation.Inject;
import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.state.exception.StateMethodException;

import java.lang.reflect.Field;

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
            Object instance = component.getClassInstance();

            // Inject logging
            if (instance != null)
                for (Field f : instance.getClass().getDeclaredFields())
                    if (f.getAnnotation(Inject.class) != null && f.getType().equals(Logger.class)) {
                        f.setAccessible(true);
                        f.set(instance, new SystemLogger());
                    }

            System.out.printf("Component loaded: %s%s", component.getName(), System.lineSeparator());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.printf("Could not load component: %s%s", component.getName(), System.lineSeparator());
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            System.out.printf("Component missing.%s%s", component.getName(), System.lineSeparator());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.printf("Cannot access attribute to inject logging for Component %s.%s", component.getName(), System.lineSeparator());
        }
    }

    @Override
    public void unload() throws StateMethodException {
        throw new StateMethodException("Component is already unloaded.");
    }
}

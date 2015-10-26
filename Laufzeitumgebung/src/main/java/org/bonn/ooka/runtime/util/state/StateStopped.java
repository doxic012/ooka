package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.exception.StateMethodException;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.state.annotation.StartMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStopped implements State {

    private Component component;

    public StateStopped(Component component) {
        this.component = component;
    }

    private void runMethod(Method method, Object... args) {
        try {
            method.invoke(args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void start() throws StateMethodException {
        Class<?> componentClass = component.getComponentClass();

//        if (!componentClass.isAnnotationPresent(StartMethod.class))
        Method startMethod = null;
        for (Method m : componentClass.getMethods()) {

            // find annotated start method that is static
            if (m.isAnnotationPresent(StartMethod.class) && Modifier.isStatic(m.getModifiers())) {
                startMethod = m;
                break;
            }
        }

        if(startMethod == null)
            throw new StateMethodException("Component does not provide annotation for StartMethod.");

        final Method finalStartMethod = startMethod;
        new Thread(() -> runMethod(finalStartMethod)).start();
        System.out.printf("Method of Component %s started: %s%s", component.getName(), finalStartMethod.getName(), System.lineSeparator());
        component.setState(new StateStarted(component));
    }

    @Override
    public void stop() throws StateMethodException {
        throw new StateMethodException("Component is already stopped.");
    }

    @Override
    public void load() throws StateMethodException {
        throw new StateMethodException("Component is already loaded.");
    }

    @Override
    public void unload() {
        component.setState(new StateUnloaded(component, (ExtendedClassLoader) component.getComponentClass().getClassLoader()));
        component.setComponentClass(null);
        System.out.printf("Reference to component class/instance deleted: %s%s", component.getName(), System.lineSeparator());
    }
}

package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.ClassComponent;
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

    @Override
    public void start() throws StateMethodException {
        final Method startMethod = component.getRunnableMethod(StartMethod.class);

        if (startMethod == null)
            throw new StateMethodException("Component does not provide annotation for StartMethod.");

        component.runComponent(startMethod, null);
        component.setState(new StateStarted(this.component));

        System.out.printf("Method of component %s started: '%s()'%s", this.component.getName(), startMethod.getName(), System.lineSeparator());
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

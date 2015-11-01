package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.state.exception.StateMethodException;
import org.bonn.ooka.runtime.util.state.annotation.StartMethod;

import java.lang.reflect.Method;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStopped implements State {

    private Component component;

    public StateStopped(Component component) {
        this.component = component;
    }

    @Override
    public void start(Object... args) throws StateMethodException {
        final Method startMethod = component.getRunnableMethod(StartMethod.class);

        if (startMethod == null)
            throw new StateMethodException("Component does not provide annotation for StartMethod.");

        component.runComponent(startMethod, args);
        component.setState(new StateStarted(this.component));

        System.out.printf("Component %s started with method %s(%s)%s", component.getName(), startMethod.getName(), args, System.lineSeparator());
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
        component.setState(new StateUnloaded(component, component.getComponentClass().getClassLoader()));
        component.setComponentClass(null);
        System.out.printf("Reference to component class/instance deleted: %s%s", component.getName(), System.lineSeparator());
    }
}

package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.state.exception.StateMethodException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStarted implements State {

    private Component component;

    public StateStarted(Component component) {
        this.component = component;
    }

    @Override
    public void start(Object... args) throws StateMethodException {
        throw new StateMethodException("Component is already started.");
    }

    @Override
    public void stop() {
        component.stopComponent();
        component.setState(new StateStopped(component));

        System.out.printf("Component stopped: %s%s", component.getName(), System.lineSeparator());
    }

    @Override
    public void load() throws StateMethodException {
        throw new StateMethodException("Component is already loaded.");
    }

    @Override
    public void unload() throws StateMethodException {
        throw new StateMethodException("Component must be stopped first.");
    }
}

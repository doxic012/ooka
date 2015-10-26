package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.exception.StateMethodNotAllowedException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStarted implements State {

    private Component component;

    public StateStarted(Component component) {
        this.component = component;
    }

    @Override
    public void start() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component is already started.");

    }

    @Override
    public void stop() {

    }

    @Override
    public void load() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Cannot load a started component.");

    }

    @Override
    public void unload() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component must be stopped first");
    }
}

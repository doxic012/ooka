package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.exception.StateMethodNotAllowedException;
import org.bonn.ooka.runtime.util.state.State;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateLoaded implements State {

    private Component component;

    public StateLoaded(Component component) {
        this.component = component;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component is not started.");
    }

    @Override
    public void load() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component is already loaded.");
    }

    @Override
    public void unload() throws StateMethodNotAllowedException {
    }
}

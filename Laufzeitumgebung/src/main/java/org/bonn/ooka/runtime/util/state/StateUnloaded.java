package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.exception.StateMethodNotAllowedException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateUnloaded implements State {

    private Component component;

    public StateUnloaded(Component component) {
        this.component = component;
    }

    @Override
    public void start() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component is not loaded.");
    }

    @Override
    public void stop() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component is not loaded");
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component is already unloaded.");
    }
}

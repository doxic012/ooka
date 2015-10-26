package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.component.Component;
import org.bonn.ooka.runtime.util.exception.StateMethodNotAllowedException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStopped implements State {

    private Component component;

    public StateStopped(Component component) {
        this.component = component;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() throws StateMethodNotAllowedException {
        throw new StateMethodNotAllowedException("Component is already stopped.");
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}

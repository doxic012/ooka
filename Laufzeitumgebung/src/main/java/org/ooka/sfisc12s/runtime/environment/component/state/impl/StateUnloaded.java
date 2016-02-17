package org.ooka.sfisc12s.runtime.environment.component.state.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.environment.component.state.State;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import java.io.IOException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateUnloaded implements State {
    private static Logger log = LoggerFactory.getRuntimeLogger(StateUnloaded.class);

    private Component component;

    public StateUnloaded(Component component) {
        this.component = component;
    }

    @Override
    public String getName() {
        return "StateUnloaded";
    }

    @Override
    public void start(Object... args) throws StateException {
        throw new StateException("Component is not loaded.");
    }

    @Override
    public void stop(Object... args) throws StateException {
        throw new StateException("Component is not loaded");
    }

    @Override
    public void load() {
        try {
            component.initialize();
            component.setState(new StateStopped(component));
            log.debug("Component initialized: %s", component.getComponentData());
        } catch (ClassNotFoundException e) {
            log.error(e, "Class not found or could not load component: %s", component.getComponentData());
        } catch (NoClassDefFoundError e) {
            log.error(e, "Component missing.%s", component.getComponentData());
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            log.error(e, "Exception.%s", component.getComponentData().getName());
        }
    }

    @Override
    public void unload() throws StateException {
        throw new StateException("Component is already unloaded.");
    }
}

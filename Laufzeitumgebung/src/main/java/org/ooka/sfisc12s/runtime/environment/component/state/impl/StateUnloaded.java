package org.ooka.sfisc12s.runtime.environment.component.state.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.state.State;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateUnloaded implements State {
    private static Logger log = LoggerFactory.getRuntimeLogger(StateUnloaded.class);

    private ComponentBase componentBase;

    public StateUnloaded(ComponentBase componentBase) {
        this.componentBase = componentBase;
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
            componentBase.initialize();

            RuntimeEnvironment re = RuntimeEnvironment.getInstance();
            re.updateCache(componentBase);
            re.injectDependencies(componentBase);

            componentBase.setState(new StateStopped(componentBase));

            log.debug("Component initialized: %s", componentBase);
        } catch (ClassNotFoundException e) {
            log.error(e, "Class not found or could not load component: %s", componentBase);
        } catch (NoClassDefFoundError e) {
            log.error(e, "Component missing.%s", componentBase);
        } catch (URISyntaxException e) {
            log.error(e, "URI could was not loaded correctly. %s", componentBase.getName());
        }catch (IOException | InstantiationException | IllegalAccessException e) {
            log.error(e, "Exception.%s", componentBase.getName());
        }
    }

    @Override
    public void unload() throws StateException {
        throw new StateException("Component is already unloaded.");
    }
}

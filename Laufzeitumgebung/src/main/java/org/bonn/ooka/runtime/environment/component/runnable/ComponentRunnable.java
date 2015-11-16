package org.bonn.ooka.runtime.environment.component.runnable;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.component.state.State;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
import org.bonn.ooka.runtime.util.Logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by steve on 16.11.15.
 */
public class ComponentRunnable implements Runnable {

    private static Logger log = LoggerFactory.getRuntimeLogger(ComponentRunnable.class);

    private Method invokeMethod;

    private Object[] methodArgs;

    private Component component;

    private State errorState;

    public ComponentRunnable(Component cmp, Method invokeMethod, Object[] methodArgs, State errorState) {
        this.invokeMethod = invokeMethod;
        this.methodArgs = methodArgs;
        this.component = cmp;
        this.errorState = errorState;
    }

    @Override
    public void run() {
        try {
            invokeMethod.invoke(component.getComponentInstance(), methodArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e, "Error in method invokation.");
            component.setState(errorState);
        }
    }
}

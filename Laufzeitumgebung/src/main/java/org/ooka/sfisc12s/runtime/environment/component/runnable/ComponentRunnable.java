package org.ooka.sfisc12s.runtime.environment.component.runnable;

import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.state.State;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

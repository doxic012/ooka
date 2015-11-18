package org.bonn.ooka.runtime.environment.event;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.environment.annotation.Observes;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
import org.bonn.ooka.runtime.util.Logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Event<E> {
    private static Logger log = LoggerFactory.getRuntimeLogger(Event.class);

    private Object source;

    private E eventData;

    public Event(Object source, E eventData) {
        this.source = source;
        this.eventData = eventData;
    }

    public E getEventData() {
        return eventData;
    }

    public void fire() {
        RuntimeEnvironment.getInstance().getComponents().forEach((name, component) -> {
            if(component.equals(source))
                return;

            for (Method m : component.getAnnotatedParameterMethods(Observes.class, getEventData().getClass())) {
                try {
                    m.invoke(component.getComponentInstance(), getEventData());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e);
                }
            }
        });
    }
}

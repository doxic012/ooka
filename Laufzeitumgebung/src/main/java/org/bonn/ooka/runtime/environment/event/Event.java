package org.bonn.ooka.runtime.environment.event;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.environment.annotation.Observes;
import org.bonn.ooka.runtime.environment.component.ComponentData;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
import org.bonn.ooka.runtime.util.Logger.Logger;
import sun.reflect.annotation.AnnotationType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Event<E> {
    private static Logger log = LoggerFactory.getRuntimeLogger(Event.class);

    private E eventType;

    public Event(E eventType) {
        this.eventType = eventType;
    }

    public E getEventType() {
        return eventType;
    }

    public void fire() {
        RuntimeEnvironment.getInstance().getComponents().forEach((name, component) -> {
            for (Method m : component.getAnnotatedParameterMethods(Observes.class, getEventType().getClass())) {
                try {
                    //TODO: Parametertyp überprüfen (Event<State>?)
                    m.invoke(component.getComponentInstance(), getEventType());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e);
                }
            }
        });
    }
}

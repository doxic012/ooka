package org.ooka.sfisc12s.runtime.environment.event;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.annotation.Observes;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RuntimeEvent<E> {
    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEvent.class);

    private Object source;

    private E eventData;

    public RuntimeEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public E getEventData() {
        return eventData;
    }

    public void setEventData(E eventData) {
        this.eventData = eventData;
    }

    public void fire() {
        if (source == null && getEventData() == null)
            return;

        RuntimeEnvironment.getInstance().getComponents().stream().filter(c -> !c.equals(source)).forEach(component -> {
            for (Method m : component.getAnnotatedParameterMethods(Observes.class, getEventData().getClass())) {
                try {
                    m.invoke(component.getComponentInstance(), getEventData());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e, "Error during event firing");
                }
            }
        });
    }
}

package org.bonn.ooka.runtime.util.component;

import org.bonn.ooka.runtime.util.exception.StateMethodException;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.state.State;
import org.bonn.ooka.runtime.util.state.StateStopped;
import org.bonn.ooka.runtime.util.state.StateUnloaded;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class Component {

    private State state;

    private String id;

    private String path;

    private String name;

    private Class<?> componentClass;

    private Thread thread;

    public Component(String name, String path, ExtendedClassLoader classLoader) {
        this.path = path;
        this.name = name;
        this.state = new StateUnloaded(this, classLoader);
    }

    public Component(String name, String path, State state) {
        this.path = path;
        this.name = name;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }

    public String getStatus() {
        return String.format("Id: %s, Pfad: %s, Name: %s, Zustand: %s", getId(), getPath(), getName(), state.toString());
    }

    public final void start(Object... args) throws StateMethodException {
        state.start(args);
    }

    public final void stop() throws StateMethodException {
        state.stop();
    }

    public final void load() throws StateMethodException {
        state.load();
    }

    public final void unload() throws StateMethodException {
        state.unload();
    }

    public Method getRunnableMethod(Class<? extends Annotation> annotationClass) {
        for (Method method : getComponentClass().getMethods())
            if (annotationClass != null && method.isAnnotationPresent(annotationClass))
                return method;

        return null;
    }

    public boolean isComponentRunning() {
        return thread != null && !thread.isInterrupted();
    }

    // Create a new thread for the method if there is no reference yet
    // Start the thread, invoke the method and delete the thread reference after that
    public final void runComponent(Method method, Object... args) {
        if (thread == null && method != null) {
            thread = new Thread(() -> {
                try {
                    method.invoke(args);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    thread = null;
                }
            });
            thread.start();
        }
    }

    public final void stopComponent() {
        if (isComponentRunning()) {
            thread.interrupt();
            thread = null;
        }
    }
}

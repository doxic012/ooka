package org.bonn.ooka.runtime.environment.component;

import org.bonn.ooka.runtime.environment.annotation.Inject;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;
import org.bonn.ooka.runtime.environment.component.state.State;
import org.bonn.ooka.runtime.environment.component.state.impl.StateUnloaded;
import org.bonn.ooka.runtime.environment.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.Logger.Impl.RuntimeLogger;
import org.bonn.ooka.runtime.util.Logger.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class Component {

    private State state;

    private String id;

    private URL path;

    private String name;

    private Class<?> componentClass;

    private Object componentInstance;

    private ExtendedClassLoader classLoader;

    public Component(URL path, String name, ExtendedClassLoader classLoader) {
        this.path = path;
        this.name = name;
        this.classLoader = classLoader;
        this.state = new StateUnloaded(this);

        try {
            classLoader.addUrl(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Component setState(State state) {
        this.state = state;
        return this;
    }

    public Component setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
        this.id = componentClass != null ? String.valueOf(componentClass.hashCode()) : null;
        return this;
    }

    protected Component setComponentInstance(Class<?> componentClass) throws IllegalAccessException, InstantiationException {
        if (componentClass == null)
            return this;

//        try {
            // instantiate only when possible
            int mod = componentClass.getModifiers();
            if (!(!Modifier.isPublic(mod) || Modifier.isAbstract(mod) || Modifier.isInterface(mod) || Modifier.isFinal(mod)))
                componentInstance = componentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.printf("ComponentClass '%s' cannot be instantiated.%s", getName(), System.lineSeparator());
//        }
        return this;
    }

    public State getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public URL getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public Object getComponentInstance() {
        return componentInstance;
    }

    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    public String getStatus() {
        return String.format("Id: %s, Pfad: %s, Name: %s, Zustand: %s", getId(), getPath().toString(), getName(), state.toString());
    }

    public Method getAnnotatedMethod(Class<? extends Annotation> annotationClass) {
        for (Method method : getComponentClass().getMethods())
            if (annotationClass != null && method.isAnnotationPresent(annotationClass))
                return method;

        return null;
    }

    protected Component injectLogger() {
        try {
            Object instance = getComponentInstance();
            if (instance != null)
                for (Field f : instance.getClass().getDeclaredFields())
                    if (f.getAnnotation(Inject.class) != null && f.getType().equals(Logger.class)) {
                        f.setAccessible(true);
                        f.set(instance, new RuntimeLogger());

                    }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.printf("Cannot access attribute to inject logging for Component %s.%s", getName(), System.lineSeparator());
        }

        return this;
    }

    public final Component start(Object... args) throws StateException {
        state.start(args);
        return this;
    }

    public final Component stop() throws StateException {
        state.stop();
        return this;
    }

    public final Component load() throws StateException {
        state.load();
        return this;
    }

    public final Component unload() throws StateException {
        state.unload();
        return this;
    }

    public abstract Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;

    public abstract boolean isComponentRunning();

    /**
     * Create a new thread for the method if there is no reference yet
     * Start the thread, invoke the method and delete the thread reference after that
     *
     * @param args Method arguments for start method
     * @throws StateException
     */
    public abstract Component startComponent(Object... args) throws StateException;

    /**
     * @param args Method arguments for stop method
     * @throws StateException
     */
    public abstract Component stopComponent(Object... args) throws StateException;

}

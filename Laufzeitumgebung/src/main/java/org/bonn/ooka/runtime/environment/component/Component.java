package org.bonn.ooka.runtime.environment.component;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.environment.annotation.Inject;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;
import org.bonn.ooka.runtime.environment.component.state.State;
import org.bonn.ooka.runtime.environment.component.state.impl.StateStarted;
import org.bonn.ooka.runtime.environment.component.state.impl.StateStopped;
import org.bonn.ooka.runtime.environment.component.state.impl.StateUnloaded;
import org.bonn.ooka.runtime.environment.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
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

    private RuntimeEnvironment runtimeEnvironment;

    private State state;

    private String id;

    private String name;

    private URL path;

    private Class<?> componentClass;

    private Object componentInstance;

    public Component(URL path, String name, RuntimeEnvironment re) {
        this.path = path;
        this.name = name;
        this.runtimeEnvironment = re;
        this.state = new StateUnloaded(this);

        try {
            getClassLoader().addUrl(path);
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

        // instantiate only when possible
        int mod = componentClass.getModifiers();
        if (!(!Modifier.isPublic(mod) || Modifier.isAbstract(mod) || Modifier.isInterface(mod) || Modifier.isFinal(mod)))
            componentInstance = componentClass.newInstance();

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
        return getRE().getClassLoader();
    }

    public RuntimeEnvironment getRE() {
        return runtimeEnvironment;
    }

    public String getStatus() {
        return String.format("Id: %s, Zustand: %s, Pfad: %s", getId(), state.getName(), getPath().toString());
    }

    public Method getAnnotatedMethod(Class<? extends Annotation> annotationClass) {
        for (Method method : getComponentClass().getMethods())
            if (annotationClass != null && method.isAnnotationPresent(annotationClass))
                return method;

        return null;
    }

    protected Component injectLogger() throws IllegalAccessException {
        Object instance = getComponentInstance();
        if (instance == null)
            return this;

        for (Field f : instance.getClass().getDeclaredFields())
            if (f.getAnnotation(Inject.class) != null && f.getType().equals(Logger.class)) {
                f.setAccessible(true);
                f.set(instance, new RuntimeLogger());
            }

        return this;
    }

    public boolean isComponentRunning() {
        return getState() instanceof StateStarted;
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

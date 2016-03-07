package org.ooka.sfisc12s.runtime.environment.component;

import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.environment.component.runnable.ComponentRunnable;
import org.ooka.sfisc12s.runtime.environment.component.scope.Scope;
import org.ooka.sfisc12s.runtime.environment.component.state.impl.StateStarted;
import org.ooka.sfisc12s.runtime.environment.component.state.impl.StateUnloaded;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.component.state.State;
import org.ooka.sfisc12s.runtime.environment.component.state.impl.StateStopped;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Component {

    private ComponentData data;

    private List<Class<?>> componentStructure = new ArrayList<>();

    private Class<?> componentClass;

    private Object componentInstance;

    public Component(URL path, String name) {
        this.data = new ComponentData(name, path, new StateUnloaded(this));

        try {
            getClassLoader().addUrl(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Component setState(State state) {
        this.data.setState(state);
        return this;
    }

    protected Component setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
        this.data.setId(componentClass != null ? String.valueOf(componentClass.hashCode()) : null);
        return this;
    }

    protected Component setComponentInstance(Class<?> componentClass) throws IllegalAccessException, InstantiationException {
        if (componentClass == null)
            return this;

        // instantiate only when possible
        if (isClassInstantiable(componentClass))
            componentInstance = componentClass.newInstance();

        return this;
    }

    public Component clear() {
        componentInstance = null;
        return this;
    }

    public ComponentData getData() {
        return data;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public Object getComponentInstance() {
        return componentInstance;
    }

    public List<Class<?>> getComponentStructure() {
        return Collections.unmodifiableList(componentStructure);
    }

    public ExtendedClassLoader getClassLoader() {
        return RuntimeEnvironment.getInstance().getClassLoader();
    }

    public String getStatus() {
        return data.toString();
    }

    public boolean isComponentRunning() {
        return this.data.getState() instanceof StateStarted;
    }

    public Method getAnnotatedMethod(Class<? extends Annotation> annotationClass) {
        if (annotationClass == null)
            return null;

        for (Method method : getComponentClass().getMethods())
            if (method.isAnnotationPresent(annotationClass))
                return method;

        return null;
    }

    public List<Method> getAnnotatedParameterMethods(Class<? extends Annotation> annotationClass, Class<?> parameterClass) {
        if (annotationClass == null || parameterClass == null)
            return null;

        List<Method> methods = new ArrayList<>();
        for (Method method : getComponentClass().getMethods())
            for (Parameter param : method.getParameters())
                if (param.isAnnotationPresent(annotationClass) &&
                        param.getParameterizedType().getTypeName().equals(parameterClass.getTypeName()))
                    methods.add(method);

        return methods;
    }

    /**
     * Create a new thread for the method if there is no reference yet
     * Start the thread, invoke the method and delete the thread reference after that
     *
     * @param args Method arguments for start method
     * @throws StateException
     */
    public Component startComponent(Object... args) throws StateException {
        if (isComponentRunning())
            throw new StateException("Component has already been started.");

        final Method startMethod = getAnnotatedMethod(StartMethod.class);
        if (startMethod == null)
            throw new StateException("Component does not provide annotation for StartMethod.");

        new Thread(new ComponentRunnable(this, startMethod, args, new StateStopped(this))).start();

        return this;
    }

    /**
     * @param args Method arguments for stop method
     * @throws StateException
     */
    public Component stopComponent(Object... args) throws StateException {
        if (!isComponentRunning())
            throw new StateException("Component is not started.");

        final Method stopMethod = getAnnotatedMethod(StopMethod.class);

        if (stopMethod == null)
            throw new StateException("Component does not provide annotation for StopMethod.");

        new Thread(new ComponentRunnable(this, stopMethod, args, new StateStopped(this))).start();

        return this;
    }

    public Component start(Object... args) throws StateException {
        this.data.getState().start(args);
        return this;
    }

    public Component stop() throws StateException {
        this.data.getState().stop();
        return this;
    }

    public Component load() throws StateException {
        this.data.getState().load();
        return this;
    }

    public Component unload() throws StateException {
        this.data.getState().unload();
        return this;
    }

    public boolean containsScope(String scope) {
        return getData().getScopes().stream().anyMatch(s -> s.getName().equals(scope));
    }

    public static boolean isClassInstantiable(Class<?> componentClass) {
        int mod = componentClass.getModifiers();
        return !(!Modifier.isPublic(mod) || Modifier.isAbstract(mod) || Modifier.isInterface(mod));
    }

    private static List<Scope> allScopes = Collections.unmodifiableList(Arrays.asList(new Scope(Scope.UnderTest), new Scope(Scope.InProduction), new Scope(Scope.UnderInspection), new Scope(Scope.InMaintenance)));

    public static List<Scope> getAllScopes() {
        return allScopes;
    }

    public abstract Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
}

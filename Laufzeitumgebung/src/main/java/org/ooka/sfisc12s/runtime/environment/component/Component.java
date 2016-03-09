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

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
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

@MappedSuperclass

@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "components", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "name", "componentType", "filePath", "scope"}))
public abstract class Component implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private String fileName;

    @Column
    private String filePath;

    @Column
    private URL url;

    @Column
    private String scope;

    @Column
    private String componentType;

    protected List<Class<?>> componentStructure = new ArrayList<>();

    private Class<?> componentClass;

    private Object componentInstance;

    private State state = new StateUnloaded(this);

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setScope(String scopes) {
        this.scope = scopes;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public String getFileName() {
        return fileName;
    }

    public URL getUrl() {
        return url;
    }

    public Component(String name, String filePath, String fileName, URL url, String type) {
        this.name = name;
        this.filePath = filePath;
        this.fileName = fileName;
        this.url = url;
        this.componentType = type;
    }

    public Component() {
    }

    public Component setState(State state) {
        this.state = state;
        return this;
    }

    protected Component setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
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

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public Object getComponentInstance() {
        return componentInstance;
    }

    public List<Class<?>> getComponentStructure() {
        return Collections.unmodifiableList(componentStructure);
    }

    public Component clear() {
        componentInstance = null;
        return this;
    }

    // TODO: von RE übergeben lassen
    public ExtendedClassLoader getClassLoader() {
        return RuntimeEnvironment.getInstance().getClassLoader();
    }

    State getState() {
        return state;
    }

    public Class<? extends State> getRawState() {
        return state.getClass();
    }

    public boolean isComponentRunning() {
        return this.getState() instanceof StateStarted;
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
        this.getState().start(args);
        return this;
    }

    public Component stop() throws StateException {
        this.getState().stop();
        return this;
    }

    public Component load() throws StateException {
        this.getState().load();
        return this;
    }

    public Component unload() throws StateException {
        this.getState().unload();
        return this;
    }

    public boolean containsScope(String scope) {
        return getScope().contains(scope); //getDto().getScopes().stream().anyMatch(s -> s.getName().equals(scope));
    }

    public boolean isJarComponent() {
        return componentType.toLowerCase().equals("jar");
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

    @Override
    public String toString() {
        return String.format("%s - State: %s, Id: %s, Pfad: %s", getName(), getState(), getId(), getFilePath().toString());
    }
}

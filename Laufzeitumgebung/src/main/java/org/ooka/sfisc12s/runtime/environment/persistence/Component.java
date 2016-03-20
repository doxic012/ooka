package org.ooka.sfisc12s.runtime.environment.persistence;

import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.environment.runnable.ComponentRunnable;
import org.ooka.sfisc12s.runtime.environment.state.impl.StateStarted;
import org.ooka.sfisc12s.runtime.environment.state.impl.StateUnloaded;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.state.State;
import org.ooka.sfisc12s.runtime.environment.state.impl.StateStopped;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.ClassUtil;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.MessageDigestUtil;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@Entity
@Table(name = "components", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "baseType", discriminatorType = DiscriminatorType.STRING)
public abstract class Component implements Serializable, Scopeable {

    private static Logger log = LoggerFactory.getRuntimeLogger(Component.class);

    /* Konstruktor */
    public Component(String fileName, URL url, Scope scope, String baseType) {
        setFileName(fileName);
        setUrl(url);
        setScope(scope);
        setBaseType(baseType);
        setChecksum();
    }

    /* Konstruktor */
    public Component(String fileName, URL url, String baseType) {
        setFileName(fileName);
        setUrl(url);
        setBaseType(baseType);
        setChecksum();
    }

    /* Konstruktor */
    public Component() {
    }

    public abstract Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String fileName;

    @Column
    private URL url;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    @Column(insertable = false, updatable = false)
    private String baseType;

    @Transient
    private String checksum;

    @Transient
    protected List<Class<?>> componentStructure = new ArrayList<>();

    @Transient
    private Class<?> componentClass;

    @Transient
    private Object componentInstance;

    @Transient
    private State state = new StateUnloaded(this);

    @Transient
    private RuntimeEnvironment runtimeEnvironment;

    @Transient
    private boolean initialized = false;

    /* Id */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /* fileName */
    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return fileName.replace("." + baseType, "");
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /* Scope */
    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    /* URL */
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /* BaseType */
    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String componentType) {
        this.baseType = componentType;
    }

    /* Checksum */
    public String getChecksum() {
        if (checksum == null)
            checksum = MessageDigestUtil.getMD5Hex(url);
        return checksum;
    }

    private void setChecksum() {
        this.checksum = MessageDigestUtil.getMD5Hex(url);
    }

    /* Component Structure */
    public List<Class<?>> getComponentStructure() {
        return Collections.unmodifiableList(componentStructure);
    }

    /* Component Class */
    public Class<?> getComponentClass() {
        return componentClass;
    }

    protected Component setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
        return this;
    }

    /* Component Instance */
    public Object getComponentInstance() {
        return componentInstance;
    }

    protected Component setComponentInstance(Class<?> componentClass) throws IllegalAccessException, InstantiationException {
        if (componentClass == null || !ClassUtil.isClassInstantiable(componentClass))
            return this;

        // instantiate only when possible
        componentInstance = componentClass.newInstance();
        log.debug("New component instance %s", componentInstance);
        return this;
    }

    /* Runtime Environment */
    public RuntimeEnvironment getRuntimeEnvironment() {
        return runtimeEnvironment;
    }

    public void setRuntimeEnvironment(RuntimeEnvironment re) {
        this.runtimeEnvironment = re;
    }

    /* State */
    public State getState() {
        return state;
    }

    public Component setState(State state) {
        this.state = state;
        return this;
    }

    public Class<? extends State> getRawState() {
        return state.getClass();
    }

    /* Initialized */
    protected void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /* Other Methods */
    public ExtendedClassLoader getClassLoader() {
        return getRuntimeEnvironment().getClassLoader();
    }

    public Component clear() {
        componentInstance = null;
        return this;
    }

    public boolean isRunning() {
        return this.getState() instanceof StateStarted;
    }

    public boolean isValid() {
        return getUrl() != null &&
                getBaseType() != null && !getBaseType().isEmpty() &&
                getChecksum() != null && !getChecksum().isEmpty();
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
        if (getComponentClass() == null || annotationClass == null || parameterClass == null)
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
        if (getComponentInstance() == null)
            throw new StateException("Component has no valid instance to start");

        if (isRunning())
            throw new StateException("Component has already been started.");

        final Method startMethod = getAnnotatedMethod(StartMethod.class);

        if (startMethod == null)
            throw new StateException("Component does not provide annotation for StartMethod.");

        new Thread(new ComponentRunnable(this, startMethod, args, new StateStopped(this))).start();

        return this;
    }

    /**
     * Stop the running thread of the component if its already started.
     *
     * @param args Method arguments for stop method
     * @throws StateException
     */
    public Component stopComponent(Object... args) throws StateException {
        if (!isRunning())
            throw new StateException("Component is not started.");

        final Method stopMethod = getAnnotatedMethod(StopMethod.class);

        if (stopMethod == null)
            throw new StateException("Component does not provide annotation for StopMethod.");

        new Thread(new ComponentRunnable(this, stopMethod, args, new StateStopped(this))).start();

        return this;
    }


    public boolean isStarted() {
        return getState() instanceof StateStarted;
    }

    public boolean isStopped() {
        return getState() instanceof StateStopped;
    }

    public boolean isUnloaded() {
        return getState() instanceof StateUnloaded;
    }

    public Component start() throws StateException, ScopeException {
        return this.start(null);
    }

    public Component start(Object... args) throws StateException, ScopeException {
        this.getState().start(args);
        return this;
    }

    public Component stop() throws StateException, ScopeException {
        return this.stop(null);
    }

    public Component stop(Object... args) throws StateException, ScopeException {
        this.getState().stop(args);
        return this;
    }

    public Component forceStop() {
        try {
            this.getState().forceStop(true, null);
        } catch (ScopeException e) {
            log.error(e, "Error while force stopping component");
        }
        return this;
    }

    public Component load() throws StateException {
        this.getState().load();
        return this;
    }

    public Component unload() throws StateException, ScopeException {
        this.getState().unload();
        return this;
    }

    public static boolean isClassInstantiable(Class<?> clazz) {
        int mod = clazz.getModifiers();
        return !(!Modifier.isPublic(mod) || Modifier.isAbstract(mod) || Modifier.isInterface(mod));
    }

    @Override
    public String toString() {
        return String.format("Id: %s, State: %s, FileName: %s , MD5 Checksum: %s, Scope: %s", getId(), getState().getName(), getFileName(), getChecksum(), getScope());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Component))
            return false;

        Component c = (Component) obj;

        // fileName, scope and baseType must be equal
        return super.equals(c) ||
                (Objects.equals(getChecksum(), c.getChecksum()) &&
                        Objects.equals(getScope(), c.getScope()));
    }
}

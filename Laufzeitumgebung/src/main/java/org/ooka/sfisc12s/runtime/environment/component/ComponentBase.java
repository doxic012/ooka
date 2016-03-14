package org.ooka.sfisc12s.runtime.environment.component;

import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.environment.component.runnable.ComponentRunnable;
import org.ooka.sfisc12s.runtime.environment.component.state.impl.StateStarted;
import org.ooka.sfisc12s.runtime.environment.component.state.impl.StateUnloaded;
import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.component.state.State;
import org.ooka.sfisc12s.runtime.environment.component.state.impl.StateStopped;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable;
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
public abstract class ComponentBase implements Serializable, Scopeable {

    private static Logger log = LoggerFactory.getRuntimeLogger(ComponentBase.class);

    /* Konstruktor */
    public ComponentBase(String fileName, URL url, Scope scope, String baseType) {
        setFileName(fileName);
        setUrl(url);
        setScope(scope);
        setBaseType(baseType);
        setChecksum();
    }

    /* Konstruktor */
    public ComponentBase(String fileName, URL url, String baseType) {
        setFileName(fileName);
        setUrl(url);
        setBaseType(baseType);
        setChecksum();
    }

    /* Konstruktor */
    public ComponentBase() {
    }

    public abstract ComponentBase initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException;

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

    protected ComponentBase setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
        return this;
    }

    /* Component Instance */
    public Object getComponentInstance() {
        return componentInstance;
    }

    protected ComponentBase setComponentInstance(Class<?> componentClass) throws IllegalAccessException, InstantiationException {
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

    public ComponentBase setState(State state) {
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

    public ComponentBase clear() {
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
    public ComponentBase startComponent(Object... args) throws StateException {
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
    public ComponentBase stopComponent(Object... args) throws StateException {
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

    public ComponentBase start() throws StateException {
        return this.start(null);
    }

    public ComponentBase start(Object... args) throws StateException {
        log.debug("Start component, current state: %s, args: %s", getState(), args);
        this.getState().start(args);
        return this;
    }

    public ComponentBase stop() throws StateException {
        return this.stop(null);
    }

    public ComponentBase stop(Object... args) throws StateException {
        log.debug("Stop component, current state: %s, args: %s", getState(), args);
        this.getState().stop(args);
        return this;
    }


    public ComponentBase load() throws StateException {
        this.getState().load();
        return this;
    }

    public ComponentBase unload() throws StateException {
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
        if (!(obj instanceof ComponentBase))
            return false;

        ComponentBase c = (ComponentBase) obj;

        // fileName, scope and baseType must be equal
        return super.equals(c) ||
                (Objects.equals(getChecksum(), c.getChecksum()) &&
                        Objects.equals(getScope(), c.getScope()));
    }
}

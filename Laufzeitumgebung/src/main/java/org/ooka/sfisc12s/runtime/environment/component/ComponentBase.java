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

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@MappedSuperclass
@Table(name = "components", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public abstract class ComponentBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private String checksum;

    @Column
    private URL url;

    @Column
    private String scope;

    @Column
    private String baseType;

    @Transient
    protected List<Class<?>> componentStructure = new ArrayList<>();

    @Transient
    private Class<?> componentClass;

    @Transient
    private Object componentInstance;

    @Transient
    private State state = new StateUnloaded(this);

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseType(String componentType) {
        this.baseType = componentType;
    }

    public void setScope(String scopes) {
        this.scope = scopes;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Long getId() {
        return id;
    }

    public String getBaseType() {
        return baseType;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public String getChecksum() {
        return checksum;
    }

    public URL getUrl() {
        return url;
    }

    public ComponentBase(String name, URL url, String scope, String baseType) throws IOException {
        this.name = name;
        this.url = url;
        this.scope = scope;
        this.baseType = baseType;
        this.checksum = getMD5Hex(this.url);
    }

    public ComponentBase() {
    }

    public ComponentBase setState(State state) {
        this.state = state;
        return this;
    }

    protected ComponentBase setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
        return this;
    }

    protected ComponentBase setComponentInstance(Class<?> componentClass) throws IllegalAccessException, InstantiationException {
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
    public ComponentBase startComponent(Object... args) throws StateException {
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
    public ComponentBase stopComponent(Object... args) throws StateException {
        if (!isComponentRunning())
            throw new StateException("Component is not started.");

        final Method stopMethod = getAnnotatedMethod(StopMethod.class);

        if (stopMethod == null)
            throw new StateException("Component does not provide annotation for StopMethod.");

        new Thread(new ComponentRunnable(this, stopMethod, args, new StateStopped(this))).start();

        return this;
    }

    public ComponentBase clear() {
        componentInstance = null;
        return this;
    }

    public boolean isValid() {
        return url != null &&
                baseType != null &&
                !baseType.isEmpty() &&
                checksum != null &&
                !checksum.isEmpty();
    }

    public ComponentBase start(Object... args) throws StateException {
        this.getState().start(args);
        return this;
    }

    public ComponentBase stop() throws StateException {
        this.getState().stop();
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

    public abstract ComponentBase initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException;

    public static String getMD5Hex(URL url) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(url.getPath())));
            byte[] digest = md.digest();

            return DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isClassInstantiable(Class<?> componentClass) {
        int mod = componentClass.getModifiers();
        return !(!Modifier.isPublic(mod) || Modifier.isAbstract(mod) || Modifier.isInterface(mod));
    }

    @Override
    public String toString() {
        return String.format("%s - State: %s, Id: %s, MD5 Checksum: %s, Scope: %s", getName(), getState(), getId(), getChecksum(), getScope());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ComponentBase))
            return false;

        ComponentBase c = (ComponentBase) obj;

        // fileName, scope and baseType must be equal
        return super.equals(c) ||
                (Objects.equals(this.getChecksum(), c.getChecksum()) &&
                        Objects.equals(this.getScope(), c.getScope()));


    }
}

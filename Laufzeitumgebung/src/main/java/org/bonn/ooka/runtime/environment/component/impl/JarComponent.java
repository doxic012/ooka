package org.bonn.ooka.runtime.environment.component.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.environment.annotation.StartMethod;
import org.bonn.ooka.runtime.environment.annotation.StopMethod;
import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;
import org.bonn.ooka.runtime.environment.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
import org.bonn.ooka.runtime.util.Logger.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Stefan on 26.10.2015.
 */
public class JarComponent extends Component {

    private static Logger log = LoggerFactory.getRuntimeLogger(JarComponent.class);

    public JarComponent(URL path, String name, RuntimeEnvironment re) {
        super(path, name, re);
    }

    private Class<?> findComponentClass() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
        JarFile jar = new JarFile(getPath().getFile());
        Enumeration<JarEntry> entries = jar.entries();

        ExtendedClassLoader loader = getClassLoader();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            // valide Klassen
            if (!entry.getName().endsWith(".class"))
                continue;

            // Classenpfad normalisieren
            Class<?> clazz = loader.loadClass(entry.getName().replaceAll("/", ".").replaceAll(".class", ""));

            boolean start = false;
            boolean stop = false;

            for (Method method : clazz.getMethods())
                if ((start = start || method.isAnnotationPresent(StartMethod.class)) &&
                        (stop = stop || method.isAnnotationPresent(StopMethod.class)))
                    return clazz;
        }

        return null;
    }

    @Override
    public Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        Class<?> loadedClass = findComponentClass();
        setComponentClass(loadedClass);
        setComponentInstance(getComponentClass());
        injectLogger();
        return this;
    }

    // Create a new thread for the method if there is no reference yet
    // Start the thread, invoke the method and delete the thread reference after that
    @Override
    public Component startComponent(Object... args) throws StateException {
        if (isComponentRunning())
            throw new StateException("Component has already been started.");

        Object instance = getComponentInstance();
        final Method startMethod = getAnnotatedMethod(StartMethod.class);

        if (startMethod == null)
            throw new StateException("Component does not provide annotation for StartMethod.");

        new Thread(() -> {
            try {
                startMethod.invoke(instance, args);
            } catch (Exception e) {
                log.error(e, "Error while exeucting startMethod.");
            }
        }).start();

        return this;
    }

    @Override
    public Component stopComponent(Object... args) throws StateException {
        if (!isComponentRunning())
            throw new StateException("Component is not started.");

        final Method stopMethod = getAnnotatedMethod(StopMethod.class);
        Object instance = getComponentInstance();

        if (stopMethod == null)
            throw new StateException("Component does not provide annotation for StopMethod.");

        try {
            stopMethod.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e, "Error while exeucting stopMethod.");
        }
        return this;
    }
}


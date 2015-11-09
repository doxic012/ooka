package org.bonn.ooka.runtime.environment.component.impl;

import org.bonn.ooka.runtime.environment.annotation.StartMethod;
import org.bonn.ooka.runtime.environment.annotation.StopMethod;
import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;
import org.bonn.ooka.runtime.environment.loader.ExtendedClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Stefan on 26.10.2015.
 */
public class JarComponent extends Component {

    private Thread thread;

    public JarComponent(URL path, String name, ExtendedClassLoader classLoader) {
        super(path, name, classLoader);
    }

    private Class<?> findComponentClass() throws ClassNotFoundException, IOException {
        JarFile jar = new JarFile(getPath().getFile());
        Enumeration<JarEntry> entries = jar.entries();

        ExtendedClassLoader loader = getClassLoader();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if(!entry.getName().endsWith(".class"))
                continue;

            Class<?> clazz = loader.loadClass(entry.getName().replaceAll("/", ".").replaceAll(".class", ""));

            // TODO: has Stop and Start method
            return clazz;
        }
        return null;
    }

    @Override
    public Component initialize() throws ClassNotFoundException, IOException {
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
        if (thread == null) {
            Object instance = getComponentInstance();
            final Method startMethod = getAnnotatedMethod(StartMethod.class);

            if (startMethod == null)
                throw new StateException("Component does not provide annotation for StartMethod.");

            thread = new Thread(() -> {
                try {
                    startMethod.invoke(instance, args);
                } catch (Exception e) {
                    System.out.println("Error in started-method: " + e.getMessage());
                } finally {
                    thread = null;
                }
            });
            thread.start();
        }
        return this;
    }

    @Override
    public Component stopComponent(Object... args) throws StateException {
        if (isComponentRunning()) {
            final Method stopMethod = getAnnotatedMethod(StopMethod.class);
            Object instance = getComponentInstance();

            if (stopMethod == null)
                throw new StateException("Component does not provide annotation for StopMethod.");

            try {
                stopMethod.invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            thread.interrupt();
            thread = null;
        }
        return this;
    }

    @Override
    public boolean isComponentRunning() {
        return thread != null && !thread.isInterrupted();
    }
}


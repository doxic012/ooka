package org.bonn.ooka.runtime.environment.component.impl;

import org.bonn.ooka.runtime.environment.annotation.StartMethod;
import org.bonn.ooka.runtime.environment.annotation.StopMethod;
import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
import org.bonn.ooka.runtime.util.Logger.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Stefan on 26.10.2015.
 */
public class JarComponent extends Component {

    private static Logger log = LoggerFactory.getRuntimeLogger(JarComponent.class);

    public JarComponent(URL path, String name) {
        super(path, name);
    }

    private Class<?> findRunnableClass() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
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
        setComponentClass(findRunnableClass());
        setComponentInstance(getComponentClass());
        injectLogger();

        return this;
    }
}


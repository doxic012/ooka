package org.ooka.sfisc12s.runtime.environment.persistence.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.environment.persistence.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import javax.persistence.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("jar")
public class JarComponent extends ComponentBase {

    private static Logger log = LoggerFactory.getRuntimeLogger(JarComponent.class);

    public JarComponent() {
        setBaseType("jar");
    }

    public JarComponent(String fileName, URL url) throws IOException {
        super(fileName, url, "jar");
    }

    public JarComponent(String fileName, URL url, Scope scope) {
        super(fileName, url, scope, "jar");
    }

    @Override
    public ComponentBase initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {
        log.debug("Initializing component (%s).", this);

        if (getComponentClass() != null) {
            setComponentInstance(getComponentClass());
            return this;
        }

        // Add url to classpath
        ExtendedClassLoader loader = getClassLoader();
        loader.addUrl(this.getUrl());
        boolean foundRunnable = false;

        JarFile jar = null;

        try {
            jar = new JarFile(getUrl().getFile());
            List<JarEntry> entries = jar.
                    stream().
                    filter(entry -> entry.getName().endsWith(".class")).
                    collect(Collectors.toList());

            for (JarEntry entry : entries) {
                log.debug("Loading class %s", entry.getName());
                // Klassenpfad normalisieren und in classloader laden
                Class<?> clazz = loader.loadClass(entry.getName().replaceAll("/", ".").replaceAll(".class", ""));

                if (!foundRunnable) {
                    boolean start = false;
                    boolean stop = false;

                    // Suche nach Start und Stop-Methode und setzen der Hauptklasse der Komponente
                    for (Method method : clazz.getMethods()) {
                        start = start || method.isAnnotationPresent(StartMethod.class);
                        stop = stop || method.isAnnotationPresent(StopMethod.class);

                        if (start && stop) {
                            setComponentClass(clazz);
                            setComponentInstance(clazz);
                            foundRunnable = true;
                            break;
                        }
                    }
                    if (foundRunnable)
                        continue;
                }

                // Klasse zur Klassenstruktur hinzufügen
                componentStructure.add(clazz);
            }
        } finally {
            if(jar != null)
            jar.close();
        }
        setInitialized(true);
        return this;
    }
}


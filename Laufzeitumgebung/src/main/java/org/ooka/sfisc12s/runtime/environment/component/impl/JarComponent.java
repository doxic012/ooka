package org.ooka.sfisc12s.runtime.environment.component.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import javax.persistence.Entity;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Entity
public class JarComponent extends Component {

    private static Logger log = LoggerFactory.getRuntimeLogger(JarComponent.class);

    public JarComponent() {
        setComponentType("Jar");
    }

    public JarComponent(String name, String filePath, String fileName, URL url) {
        super(name, filePath, fileName, url, "jar");
    }

    @Override
    public Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        log.debug("Initializing component (%s).", this);

        if (getComponentClass() != null) {
            setComponentInstance(getComponentClass());
            return this;
        }


        JarFile jar = new JarFile(getUrl().getFile());
        Enumeration<JarEntry> entries = jar.entries();
        ExtendedClassLoader loader = getClassLoader();


        try {
            loader.addUrl(this.getUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        boolean foundRunnable = false;
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            // valide Klassen
            if (!entry.getName().endsWith(".class"))
                continue;

            // Klassenpfad normalisieren und in classloader laden
            Class<?> clazz = loader.loadClass(entry.getName().replaceAll("/", ".").replaceAll(".class", ""));

            if (!foundRunnable) {
                boolean start = false;
                boolean stop = false;

                // Suche nach Start und Stop-Methode und setzen der Hauptklasse der Komponente
                for (Method method : clazz.getMethods())
                    if ((start = start || method.isAnnotationPresent(StartMethod.class)) &&
                            (stop = stop || method.isAnnotationPresent(StopMethod.class))) {
                        setComponentClass(clazz);
                        setComponentInstance(clazz);
                        foundRunnable = true;
                        break;
                    }

                if (foundRunnable)
                    continue;
            }

            // Klasse zur Klassenstruktur hinzufügen
            componentStructure.add(clazz);
        }

        return this;
    }
}


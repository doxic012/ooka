package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.environment.component.ComponentMap;
import org.ooka.sfisc12s.runtime.environment.event.RuntimeEvent;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.RuntimeLogger;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Consumer;

public class RuntimeEnvironment implements ContextDependencyInjector {

    private Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ComponentMap components = new ComponentMap();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private static RuntimeEnvironment instance = null;

    public static RuntimeEnvironment getInstance() {
        return instance == null ? instance = new RuntimeEnvironment() : instance;
    }

    /*
    Iteration über alle declared fields der Klasseninstanz einer Komponente und Injektion zugehöriger Klasseninstanzen
    */
    private Consumer<Component> inject = (c) -> {
        log.debug("Injecting into component (%s).", c.getComponentData());
        Object component = c.getComponentInstance();

        if (component == null) {
            log.debug("injected component (%s) instance is null.", c.getComponentData());
            return;
        }

        for (Field f : component.getClass().getDeclaredFields()) {
            try {
                if (f.isAnnotationPresent(Inject.class)) {
                    boolean accessible = f.isAccessible();

                    if (f.getType().equals(Logger.class)) {
                        f.setAccessible(true);
                        f.set(component, LoggerFactory.getRuntimeLogger(component.getClass()));
                    } else if (f.getType().equals(RuntimeEvent.class)) {
                        f.setAccessible(true);
                        f.set(component, new RuntimeEvent<>(this, c.getComponentData()));
                    }

                    f.setAccessible(accessible);
                }
            } catch (IllegalAccessException ex) {
                log.error(ex, "Error while injecting fields into component (%s).", c.getComponentData());
            }
        }
    };

    /*
    Iteration über alle declared fields der Klasseninstanz einer Komponente, und entfernen aller Referenzen aus
    Feldern mit @Inject
     */
    private Consumer<Component> removeInjections = (c) -> {
        log.debug("Removing injections from component (%s).", c.getComponentData());
        Object component = c.getComponentInstance();

        if (component == null) {
            log.debug("injected component (%s) instance is null.", c.getComponentData());
            return;
        }

        for (Field f : component.getClass().getDeclaredFields()) {
            try {
                if (f.isAnnotationPresent(Inject.class)) {
                    boolean accessible = f.isAccessible();
                    f.set(component, null);
                    f.setAccessible(accessible);
                }
            } catch (IllegalAccessException ex) {
                log.error(ex, "Error while removing injected fields from component (%s).", c.getComponentData());
            }
        }
    };

    private RuntimeEnvironment() {
        components.onAdd(inject);
        components.onRemove(removeInjections);
    }

    public Logger getLogger() {
        return log;
    }

    @Override
    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ComponentMap getComponents() {
        return components;
    }
}
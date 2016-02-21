package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.Reference;
import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.environment.component.ComponentMap;
import org.ooka.sfisc12s.runtime.environment.event.RuntimeEvent;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RuntimeEnvironment implements ContextDependencyInjector {

    private Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ComponentMap components = new ComponentMap();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private static RuntimeEnvironment instance = null;

    public static RuntimeEnvironment getInstance() {
        return instance == null ? instance = new RuntimeEnvironment() : instance;
    }


    // Iteration über alle declared fields der Klasseninstanz einer Komponente und Injektion zugehöriger Klasseninstanzen
    private Consumer<Component> processInections = (component) -> {
        log.debug("Injecting into component (%s).", component.getData());
        Object instance = component.getComponentInstance();

        if (instance == null) {
            log.debug("injected component (%s) instance is null.", component.getData());
            return;
        }

        for (Field f : instance.getClass().getDeclaredFields()) {
            try {
                if (f.isAnnotationPresent(Inject.class)) {
                    boolean accessible = f.isAccessible();
                    Class<?> fieldClass = f.getType();
                    Object inject = null;

                    // utility classes
                    if (fieldClass.equals(Logger.class))
                        inject = LoggerFactory.getRuntimeLogger(instance.getClass());
                    else if (fieldClass.equals(RuntimeEvent.class))
                        inject = new RuntimeEvent<>(this, component.getData());
                    else {
                        // TODO: Look inside ComponentMap
                        String ref = f.isAnnotationPresent(Reference.class) ? f.getAnnotation(Reference.class).name() : null;

                        components.forEach((name, comp) -> {
                            //TODO: Filter auf scopes

                            Stream struct = comp.getComponentStructure()
                                    .parallelStream()
                                    .filter(c -> c.equals(fieldClass) || (ref != null && c.getTypeName().equals(ref)));


                        });
                    }

                    if (inject != null) {
                        f.setAccessible(true);
                        f.set(instance, inject);
                        f.setAccessible(accessible);
                    }
                }
            } catch (IllegalAccessException ex) {
                log.error(ex, "Error while injecting fields into component (%s).", component.getData());
            }
        }
    };

    // Iteration über alle declared fields der Klasseninstanz einer Komponente, und entfernen aller Referenzen aus
    // Feldern mit @Inject
    private Consumer<Component> removeInjections = (c) -> {
        log.debug("Removing injections from component (%s).", c.getData());
        Object component = c.getComponentInstance();

        if (component == null) {
            log.debug("injected component (%s) instance is null.", c.getData());
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
                log.error(ex, "Error while removing injected fields from component (%s).", c.getData());
            }
        }
    };

    private RuntimeEnvironment() {
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
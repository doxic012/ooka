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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RuntimeEnvironment implements ContextDependencyInjector {

    private Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ComponentMap components = new ComponentMap();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    // Get list of components of componentMap only
    private List<Component> allComponents;

    // Get the list of each components runnable
    private List<Object> componentRunnables;

    // get a list of all classes available in all components
    private List<Class<?>> componentClasses;

    private static RuntimeEnvironment instance = null;

    public static RuntimeEnvironment getInstance() {
        return instance == null ? instance = new RuntimeEnvironment() : instance;
    }

    // Iteration über alle declared fields der Klasseninstanz einer Komponente und Injektion zugehöriger Klasseninstanzen
    public void processInjections(Component component) {
        log.debug("Injecting into component (%s).", component.getData());
        Object instance = component.getComponentInstance();

        if (instance == null) {
            log.debug("injected component (%s) instance is null.", component.getData());
            return;
        }

        //TODO: Filter auf scopes
        //.filter((entry) -> entry.getValue() == null)


        for (Field f : instance.getClass().getDeclaredFields()) {
            try {
                if (f.isAnnotationPresent(Inject.class)) {
                    boolean accessible = f.isAccessible();
                    Class<?> fieldClass = f.getType();
                    Object inject = null;

                    // utility classes
                    if (fieldClass.equals(Logger.class)) {
                        inject = LoggerFactory.getRuntimeLogger(instance.getClass());
                    } else if (fieldClass.equals(RuntimeEvent.class)) {
                        inject = new RuntimeEvent<>(this, component.getData());
                    } else {
                        // TODO: Look inside ComponentMap
                        String ref = f.isAnnotationPresent(Reference.class) ? f.getAnnotation(Reference.class).name() : null;

                        log.debug("looking for class: %s, typename: %s", fieldClass.getName(), fieldClass.getTypeName());

                        componentClasses.parallelStream().forEach(c -> log.debug("looking in available classes: %s, equals: %s, typename: %s, equals: %s, c is assignable: %s, fieldClass is assignable: %s, c.isInstance: %s, fieldClass.isInstance: %s", c.getName(), c.equals(fieldClass), c.getTypeName(), c.getTypeName().equals(ref), c.isAssignableFrom(fieldClass), fieldClass.isAssignableFrom(c), c.isInstance(fieldClass), fieldClass.isInstance(c)));

                        List<Class<?>> injectClasses = componentClasses.
                                parallelStream().
                                filter(c -> fieldClass.isAssignableFrom(c) && (ref == null || c.getSimpleName().equals(ref))).
                                collect(Collectors.toList());

                        //TODO: store mapping in runtime environment

                        if (injectClasses.size() > 1) {
                            // TODO: throw new exception
                            log.debug("Error while injecting instance into field '%s' for component '%s': More than one class reference available: %s", f.getName(), component, injectClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
                            continue;
                        }

                        Class<?> classFound = injectClasses.get(0);

//                        componentRunnables.parallelStream().forEach(o -> log.debug("runnable: %s, equals: %s, assignable: %s, is instance: %s", o, o.getClass().equals(classFound), classFound.isAssignableFrom(o.getClass()), classFound.isInstance(o)));

                        // TODO: check if class matches component runnable instance
                        if ((inject = componentRunnables.parallelStream().filter(classFound::isInstance).findFirst().get()) == null)
                            inject = classFound.newInstance();
                    }

                    if (inject != null) {
                        f.setAccessible(true);
                        f.set(instance, inject);
                        f.setAccessible(accessible);
                    }
                }
            } catch (IllegalAccessException | InstantiationException ex) {
                log.error(ex, "Error while injecting fields into component (%s).", component.getData());
            }
        }
    }

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

    // TODO: Parallel streams?
    private Consumer<Component> updateInternalLists = (c) -> {
        allComponents = components.
                entrySet().
                parallelStream().
                map(Map.Entry::getValue).
                collect(Collectors.toList());
        componentRunnables = allComponents.
                parallelStream().
                map(Component::getComponentInstance).
                collect(Collectors.toList());
        componentClasses = allComponents.
                parallelStream().
                map(Component::getComponentStructure).  // get all list<class> of all component structures
                flatMap(Collection::stream).            // map all collections to a single flat map
                filter(Component::isClassInstantiable). // select only instantiable classes
                collect(Collectors.toList());
    };

    private RuntimeEnvironment() {
        components.onAdd(updateInternalLists);
        components.onRemove(updateInternalLists);
//        components.onRemove(removeInjections);
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
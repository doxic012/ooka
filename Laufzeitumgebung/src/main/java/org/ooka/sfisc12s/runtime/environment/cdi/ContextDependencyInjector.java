package org.ooka.sfisc12s.runtime.environment.cdi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.Reference;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.environment.component.scope.Scope;
import org.ooka.sfisc12s.runtime.environment.component.state.impl.StateStarted;
import org.ooka.sfisc12s.runtime.environment.event.RuntimeEvent;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by steve on 17.02.16.
 */
public abstract class ContextDependencyInjector {

    private static Logger log = LoggerFactory.getRuntimeLogger(ContextDependencyInjector.class);

    private HashMap<String, Component> componentMap = new HashMap<>();//FXCollections.observableHashMap();

    // Get list of componentMap of componentMap only
    private List<Component> componentCache;

    // Get the list of each componentMap runnable
    private List<Object> runnableCache;

    // get a list of all classes available from all components in the componentMap
    private List<Class<?>> classCache;

    // Overview of all fields and mapped object instances for all components
    private Map<Component, Map<Field, Object>> injectionCache = new HashMap<>();

    public HashMap<String, Component> getComponentMap() {
        return componentMap;
    }

    // Iteration über alle declared fields der Klasseninstanz einer Komponente und Injektion zugehöriger Klasseninstanzen
    public void injectDependencies(Component component) {
        log.debug("Injecting into component (%s).", component.getData());
        Object instance = component.getComponentInstance();

        if (instance == null) {
            log.debug("injected component (%s) instance is null.", component.getData());
            return;
        }

        //TODO: Filter auf scopes
        //.filter((entry) -> entry.getValue() == null)

        injectionCache.get(component).replaceAll((f, o) -> {
            if (o != null)
                return o;

            boolean accessible = f.isAccessible();
            Class<?> fieldClass = f.getType();
            Object inject;

            try {
                // utility classes
                if (fieldClass.equals(Logger.class)) {
                    inject = LoggerFactory.getRuntimeLogger(instance.getClass());
                } else if (fieldClass.equals(RuntimeEvent.class)) {
                    inject = new RuntimeEvent<>(this, component.getData());
                } else {
                    String ref = f.isAnnotationPresent(Reference.class) ? f.getAnnotation(Reference.class).name() : null;

                    // check if class matches component runnable instance
                    List<Object> matchingRunnables = runnableCache.stream().
                            filter(fieldClass::isInstance).
                            filter(i -> ref == null || i.getClass().getSimpleName().equals(ref)).
                            collect(Collectors.toList());

//                    if (matchingRunnables.size() == 0)
//                        log.debug("No matching classes found for field '%s'", f.getName());
                    if (matchingRunnables.size() == 1)
                        inject = matchingRunnables.get(0);
                    else {
                        log.debug("looking for class: %s, typename: %s", fieldClass.getName(), fieldClass.getTypeName());

                        List<Class<?>> injectClasses = classCache.stream().
                                filter(c -> fieldClass.isAssignableFrom(c) && (ref == null || c.getSimpleName().equals(ref))).
                                collect(Collectors.toList());

                        if (injectClasses.size() != 1) {
                            log.debug("Error while injecting instance into field '%s' for component '%s': zero ore more than one class references available for class %s: %s", f.getName(), injectClasses.size(), System.lineSeparator(), component, injectClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
                            return null;
                        }

                        inject = injectClasses.get(0).newInstance();
                    }
                }

                if (inject != null) {
                    log.debug("Injecting '%s' into field '%s'", inject.getClass().getSimpleName(), f.getName());

                    f.setAccessible(true);
                    f.set(instance, inject);
                    return inject;
                }

            } catch (IllegalAccessException | InstantiationException ex) {
                log.error(ex, "Error while injecting fields into component (%s).", component.getData());
            } finally {
                f.setAccessible(accessible);
            }

            return null;
        });
    }

    // Iteration über alle declared fields der Klasseninstanz einer Komponente,
    // entfernen aller Referenzen aus Feldern mit @Inject
    public void removeDependencies(Component component) {
        log.debug("Removing injections from component (%s).", component.getData());
        Object instance = component.getComponentInstance();

        if (instance == null) {
            log.debug("injected component (%s) instance is null.", component.getData());
            return;
        }

        // remove all instances from fields
        injectionCache.get(instance).replaceAll((f, o) -> {
            boolean accessible = f.isAccessible();
            try {
                f.setAccessible(true);
                f.set(instance, null);

                return null;
            } catch (IllegalAccessException ex) {
                log.error(ex, "Error while removing injected fields from component (%s).", component.getData());
            } finally {
                f.setAccessible(accessible);
            }

            return o;
        });
    }

    public void updateComponentInjection(Component component) {
        updateComponentInjection(component, false);
    }

    public void updateComponentInjection(Component component, boolean remove) {
        log.debug("Searching for references to component instance to %s (%s)", remove ? "remove" : "inject", component.getData());
        Object instance = component.getComponentInstance();

        if (!remove && instance == null) {
            log.debug("Components instance is null and cannot be injected (%s).", component.getData());
            return;
        }

        Class<?> instanceClass = instance.getClass();
        Object inject = remove ? null : instance;

        // select all other components and inject current component instance in eligible fields
        injectionCache.forEach((c, m) -> {
            if (c.equals(component))
                return;

            m.replaceAll((f, o) -> {
                Class<?> fieldClass = f.getType();
                String ref = f.isAnnotationPresent(Reference.class) ? f.getAnnotation(Reference.class).name() : null;

                // field is not of the components class type
                if (!(fieldClass.isAssignableFrom(instanceClass) && (ref == null || instanceClass.getSimpleName().equals(ref))))
                    return o;

                boolean accessible = f.isAccessible();

                try {
                    f.setAccessible(true);
                    f.set(c.getComponentInstance(), inject);

                    return inject;
                } catch (IllegalAccessException ex) {
                    log.error(ex, "Error while injecting/removing component instance (%s) into other components.", c.getData());
                } finally {
                    f.setAccessible(accessible);
                }

                return null;
            });
        });
    }

    public void updateCache(Component component) {
        // select the list<Component> for all entries
        componentCache = getComponentMap().entrySet().stream().
                map(Map.Entry::getValue).
                collect(Collectors.toList());

        // select all instantiated objects from the component-list that are currently running and not null
        runnableCache = componentCache.stream().
                filter(c -> c.getComponentInstance() != null && c.isComponentRunning()).
                filter(c -> !c.containsScope(Scope.InMaintenance)).
                map(Component::getComponentInstance).
                collect(Collectors.toList());

        // get all list<class> of all component structures
        // map all collections to a flat List<class>
        // select only instantiable classes
        classCache = componentCache.stream().
                map(Component::getComponentStructure).
                flatMap(Collection::stream).
                filter(Component::isClassInstantiable).
                collect(Collectors.toList());

        // Update the list of all fields that are annotated with @Inject and associate it with no mapping
        // Or delete the entry from the cache when the component is not present in the component cache (anymore)
        injectionCache.
                compute(component, (c, m) -> {
                    if (c.getComponentInstance() == null || !componentCache.contains(component))
                        return null;
                    else if (m != null)
                        return m;

                    m = new HashMap<>();
                    for (Field f : c.getComponentInstance().getClass().getDeclaredFields())
                        if (f.isAnnotationPresent(Inject.class))
                            m.put(f, null);

                    return m.size() == 0 ? null : m;
                });
    }
}


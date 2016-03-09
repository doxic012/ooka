package org.ooka.sfisc12s.runtime.environment.cdi;

import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.Reference;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.event.RuntimeEvent;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ContextDependencyInjector {

    private static Logger log = LoggerFactory.getRuntimeLogger(ContextDependencyInjector.class);

    // Get list of components
    private List<ComponentBase> componentBaseCache = new ArrayList<>();

    // Get the list of each component runnable
    private List<Object> runnableCache;

    // get a list of all classes available from all components
    private List<Class<?>> classCache;

    // Overview of all fields and mapped object instances for all components
    private Map<ComponentBase, Map<Field, Object>> injectionCache = new HashMap<>();

    protected List<ComponentBase> getComponents() {
        return componentBaseCache;
    }

    // Iteration über alle declared fields der Klasseninstanz einer Komponente und Injektion zugehöriger Klasseninstanzen
    public void injectDependencies(ComponentBase componentBase) {
        log.debug("Injecting into component (%s).", componentBase.toString());
        Object instance = componentBase.getComponentInstance();

        if (instance == null) {
            log.debug("injected component (%s) instance is null.", componentBase.toString());
            return;
        }

        injectionCache.get(componentBase).replaceAll((f, o) -> {
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
                    inject = new RuntimeEvent<>(this, componentBase.toString());
                } else {
                    String ref = f.isAnnotationPresent(Reference.class) ? f.getAnnotation(Reference.class).name() : null;

                    // check if class matches component runnable instance
                    List<Object> matchingRunnables = runnableCache.stream().
                            filter(fieldClass::isInstance).
                            filter(i -> ref == null || i.getClass().getSimpleName().equals(ref)).
                            collect(Collectors.toList());

                    if (matchingRunnables.size() == 1)
                        inject = matchingRunnables.get(0);
                    else {
                        log.debug("looking for class: %s, typename: %s", fieldClass.getName(), fieldClass.getTypeName());

                        List<Class<?>> injectClasses = classCache.stream().
                                filter(c -> fieldClass.isAssignableFrom(c) && (ref == null || c.getSimpleName().equals(ref))).
                                collect(Collectors.toList());

                        if (injectClasses.size() != 1) {
                            log.debug("Error while injecting instance into field '%s' for component '%s': zero ore more than one class references available for class %s: %s", f.getName(), injectClasses.size(), System.lineSeparator(), componentBase, injectClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
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
                log.error(ex, "Error while injecting fields into component (%s).", componentBase.toString());
            } finally {
                f.setAccessible(accessible);
            }

            return null;
        });
    }

    // Iteration über alle declared fields der Klasseninstanz einer Komponente,
    // entfernen aller Referenzen aus Feldern mit @Inject
    public void removeDependencies(ComponentBase componentBase) {
        log.debug("Removing injections from component (%s).", componentBase.toString());
        Object instance = componentBase.getComponentInstance();

        if (instance == null) {
            log.debug("injected component (%s) instance is null.", componentBase.toString());
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
                log.error(ex, "Error while removing injected fields from component (%s).", componentBase.toString());
            } finally {
                f.setAccessible(accessible);
            }

            return o;
        });
    }

    public void updateComponentInjection(ComponentBase componentBase) {
        updateComponentInjection(componentBase, false);
    }

    public void updateComponentInjection(ComponentBase componentBase, boolean remove) {
        log.debug("Searching for references to component instance to %s (%s)", remove ? "remove" : "inject", componentBase.toString());
        Object instance = componentBase.getComponentInstance();

        if (!remove && instance == null) {
            log.debug("Components instance is null and cannot be injected (%s).", componentBase.toString());
            return;
        }

        Class<?> instanceClass = instance.getClass();
        Object inject = remove ? null : instance;

        // select all other components and inject current component instance in eligible fields
        injectionCache.forEach((c, m) -> {
            if (c.equals(componentBase))
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
                    log.error(ex, "Error while injecting/removing component instance (%s) into other components.", c.toString());
                } finally {
                    f.setAccessible(accessible);
                }

                return null;
            });
        });
    }

    public void updateCache(ComponentBase componentBase) {
        // select the list<Component> for all entries
//        componentCache = getComponents().entrySet().stream().
//                map(Map.Entry::getValue).
//                collect(Collectors.toList());

        // select all instantiated objects from the component-list that are currently running and not null
        runnableCache = componentBaseCache.stream().
                filter(c -> c.getComponentInstance() != null && c.isComponentRunning()).
//                filter(c -> !c.containsScope(Scope.InMaintenance)).
                map(ComponentBase::getComponentInstance).
                collect(Collectors.toList());

        // get all list<class> of all component structures
        // map all collections to a flat List<class>
        // select only instantiable classes
        classCache = componentBaseCache.stream().
                map(ComponentBase::getComponentStructure).
                flatMap(Collection::stream).
                filter(ComponentBase::isClassInstantiable).
                collect(Collectors.toList());

        // Update the list of all fields that are annotated with @Inject and associate it with no mapping
        // Or delete the entry from the cache when the component is not present in the component cache (anymore)
        injectionCache.
                compute(componentBase, (c, m) -> {
                    if (c.getComponentInstance() == null || !componentBaseCache.contains(componentBase))
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


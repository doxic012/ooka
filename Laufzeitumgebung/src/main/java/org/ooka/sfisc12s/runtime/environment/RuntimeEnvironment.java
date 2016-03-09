package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.environment.component.dao.ComponentDAO;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;

public class RuntimeEnvironment extends ContextDependencyInjector {

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ComponentDAO dao = new ComponentDAO();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private RuntimeEnvironment() {
        List<Component> components = dao.readAll(); // TODO: where filter auf scope
        if (components != null && !components.isEmpty()) {
            log.debug("List of current component dtos is loading");

            components.forEach(component -> {
                try {
                    getClassLoader().addUrl(component.getUrl()); // Add url to classpath only
                    getComponents().add(component.load());// add component to lzu
                } catch (StateException | URISyntaxException e) {
                    log.error(e, "Error while loading Component %s", component.toString());
                }
            });
        }
    }

    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    public static RuntimeEnvironment getInstance() {
        return instance == null ? instance = new RuntimeEnvironment() : instance;
    }

    public static <T> Consumer<T> measure(Consumer<T> consumer) {
        return (t) -> {

            long bestTime = -1;

            for (int i = 0; i < 3; i++) {
                long time = System.nanoTime();
                consumer.accept(t);

                time = System.nanoTime() - time;

                System.out.println(String.format("+++ measure: Iteration: %s, current time: %s", i + 1, time));

                if (bestTime == -1 || time < bestTime)
                    bestTime = time;
            }
            System.out.println(String.format("+++ measure: Best time: %s", bestTime));
        };
    }

    public static <T> Consumer<T> measure(Consumer<T> consumer, int measurements) {
        return (t) -> {

            long bestTime = -1;

            for (int i = 0; i < measurements; i++) {
                long time = System.nanoTime();
                consumer.accept(t);

                time = System.nanoTime() - time;

                System.out.println(String.format("+++ measure: Iteration: %s, current time: %s", i + 1, time));

                if (bestTime == -1 || time < bestTime)
                    bestTime = time;
            }
            System.out.println(String.format("+++ measure: Best time: %s", bestTime));
        };
    }

    @Override
    public Component addComponent(Component c) {
        return null;
    }

    @Override
    public Component getComponent(String path, String name, String type) {
        return dao.read(path, name, type);
    }

    @Override
    public Component removeComponent(Component c) {
        return null;
    }
}
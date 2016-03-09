package org.ooka.sfisc12s.runtime.environment;

import javafx.collections.MapChangeListener;
import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.environment.component.ComponentFactory;
import org.ooka.sfisc12s.runtime.environment.component.dao.ComponentDAO;
import org.ooka.sfisc12s.runtime.environment.component.dto.ComponentDTO;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public class RuntimeEnvironment extends ContextDependencyInjector {

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ComponentDAO dao = new ComponentDAO();

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private RuntimeEnvironment() {
        List<ComponentDTO> dtos = dao.readAll(); // TODO: where filter auf scope
        if (dtos != null && !dtos.isEmpty()) {
            log.debug("List of current component dtos is loading");

            dtos.forEach(dto -> {
                try {
                    Component c = ComponentFactory.createComponent(dto);

                    if (c != null) {
                        // add component to lzu
                        getComponents().put(dto.getName(), c.load());
                    } else {
                        // Add url to classpath only
                        getClassLoader().addUrl(dto.getPath());
                    }

                } catch (StateException | URISyntaxException e) {
                    log.error(e, "Error while loading Component %s", dto);
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
}
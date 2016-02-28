package org.ooka.sfisc12s.runtime.environment;

import javafx.collections.MapChangeListener;
import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;
import java.util.function.Consumer;

public class RuntimeEnvironment extends ContextDependencyInjector {

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private RuntimeEnvironment() {
//        getComponentMap().addListener((MapChangeListener<String, Component>)
//                change -> {
//                    log.debug("Updating cache");
//                    updateCache(change.wasAdded() ? change.getValueAdded() : change.getValueRemoved());
//                });
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
import org.ooka.sfisc12s.runtime.environment.annotation.Observes;
import org.ooka.sfisc12s.runtime.environment.component.ComponentData;
import org.ooka.sfisc12s.runtime.environment.event.Event;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;

public class TestClass implements TestInterface {

    private boolean running;

    @Inject
    private Logger logger;

    @Inject
    private Event<ComponentData> event;

    @Override
    public void test(String args) {
        try {
            while (running) {
                logger.debug("TestClass, arguments: " + args);
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
        }
    }

    @StartMethod
    public void start(String args) {
        running = true;
        event.fire();
        test(args);
    }

    @StopMethod
    public void stop() {
        running = false;
        event.fire();

        logger.debug("TestClass: Stop method executed");
    }

    public void notify(@Observes ComponentData eventData) {
        logger.debug("TestClass: component event fired: %s current State: %s.", eventData.getName(), eventData.getRawState().getName());
    }
}

import org.ooka.sfisc12s.runtime.environment.annotation.Observes;
import org.ooka.sfisc12s.runtime.environment.component.ComponentData;
import org.ooka.sfisc12s.runtime.environment.event.RuntimeEvent;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;

public class TestClass implements TestInterface {

    private boolean running;

    @Inject
    private Logger logger;

    @Inject
    private RuntimeEvent<ComponentData> runtimeEvent;

    @Override
    public void test(String args) {
        try {
            while (running) {
                logger.debug("TestClass, arguments: " + args);
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @StartMethod
    public void start(String args) {
        running = true;
        runtimeEvent.fire();
        test(args);
    }

    @StopMethod
    public void stop() {
        running = false;
        runtimeEvent.fire();

        logger.debug("TestClass: Stop method executed");
    }

    public void notify(@Observes ComponentData eventData) {
        logger.debug("TestClass: component runtimeEvent fired: %s current State: %s.", eventData.getName(), eventData.getRawState().getName());
    }
}

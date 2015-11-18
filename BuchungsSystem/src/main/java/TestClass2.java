import org.bonn.ooka.runtime.environment.annotation.Inject;
import org.bonn.ooka.runtime.environment.annotation.Observes;
import org.bonn.ooka.runtime.environment.annotation.StartMethod;
import org.bonn.ooka.runtime.environment.annotation.StopMethod;
import org.bonn.ooka.runtime.environment.component.ComponentData;
import org.bonn.ooka.runtime.environment.event.Event;
import org.bonn.ooka.runtime.util.Logger.Logger;

public class TestClass2 {

    private boolean running;

    @Inject
    private Logger logger;

    @Inject
    private Event<ComponentData> event;

    @StartMethod
    public void start(String args) {
        running = true;
        event.fire();

        while (running) {
            logger.debug("TestClass 2 startmethod");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @StopMethod
    public void stop() {
        running = false;
        event.fire();

        logger.debug("TestClass2: Stop method executed");
    }


    public void notify(@Observes ComponentData eventData) {
        logger.debug("TestClass2: component event fired: %s current State: %s.", eventData.getName(), eventData.getRawState().getName());
    }
}

import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.Observes;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.environment.component.ComponentData;
import org.ooka.sfisc12s.runtime.environment.event.RuntimeEvent;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

public class TestClass2 {

    private boolean running;

    @Inject
    private Logger logger;

    @Inject
    private RuntimeEvent<ComponentData> runtimeEvent;

    @StartMethod
    public void start(String args) {
        running = true;
        runtimeEvent.fire();

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
        runtimeEvent.fire();

        logger.debug("TestClass2: Stop method executed");
    }


    public void notify(@Observes ComponentData eventData) {
        logger.debug("TestClass2: component runtimeEvent fired: %s current State: %s.", eventData.getName(), eventData.getRawState().getName());
    }
}

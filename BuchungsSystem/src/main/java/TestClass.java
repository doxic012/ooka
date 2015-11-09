import org.bonn.ooka.runtime.util.Logger.Logger;
import org.bonn.ooka.runtime.environment.annotation.Inject;
import org.bonn.ooka.runtime.environment.annotation.StartMethod;
import org.bonn.ooka.runtime.environment.annotation.StopMethod;

public class TestClass implements TestInterface {

    private boolean running = true;

    @Inject
    private Logger logger;

    public TestClass() {
    }

    @Override
    public void test(String args) {
        try {
            while (running) {
                logger.debug("TestClass, arguments: " + args);
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
        }
    }

    @StartMethod
    public void start(String args) {
        test(args);
    }

    @StopMethod
    public void stop() {
        running = false;
        logger.debug("Stop method executed");
    }
}

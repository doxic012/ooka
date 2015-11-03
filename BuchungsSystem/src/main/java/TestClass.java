import org.bonn.ooka.runtime.util.Logger.Logger;
import org.bonn.ooka.runtime.util.annotation.Inject;
import org.bonn.ooka.runtime.util.annotation.StartMethod;

public class TestClass implements TestInterface {

    @Inject
    private Logger logger;

    public TestClass() {
    }

    @Override
    public void test(String args) {
        logger.debug("TestClass, arguments: " + args);
    }

    @StartMethod
    public void start(String args) {
        test(args);
    }
}

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
        System.out.println("Logger is null : " + (logger == null));

        if (logger != null)
            logger.debug("test, arguments: " + args);
    }

    @StartMethod
    public static void start(String args) {
        TestClass t = new TestClass();
        t.test(args);
    }
}

import org.bonn.ooka.runtime.util.state.annotation.StartMethod;

public class TestClass implements TestInterface {
    public TestClass() {
        System.out.println("TestClass Constructor");
    }

    @Override
    public void test(String args) {
        System.out.println("test, arguments: "+args);
    }

    @StartMethod
    public static void start(String args) {
        TestClass t = new TestClass();
        t.test(args);
    }
}

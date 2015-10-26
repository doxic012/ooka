import org.bonn.ooka.runtime.util.state.annotation.StartMethod;

public class TestClass implements TestInterface {
    public TestClass() {
        System.out.println("TestClass Constructor");
    }

    @Override
    public void test() {
        System.out.println("test");
    }

    @StartMethod
    public static void start() {
        TestClass t = new TestClass();
        t.test();
    }
}

import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;

import java.util.Random;

public class TestClass {
    private int id;

    private String name;

    private boolean started = false;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestClass() {
        name = "testclass";
    }

    @StartMethod
    public void start() {
        id = new Random(10).nextInt();
        started = true;
    }

    @StopMethod
    public void stop() {
        started = false;
    }
}

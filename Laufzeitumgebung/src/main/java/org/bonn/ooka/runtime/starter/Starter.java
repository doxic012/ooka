package org.bonn.ooka.runtime.starter;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.util.entity.CommandClassLoader;

public class Starter {

    public static void main(String[] args) {
        RuntimeEnvironment runtime = new RuntimeEnvironment();

        // [\h+\w]*
        runtime
                .addCommand("load class", "[\\h+(\\w|\"\\w\")]*", new CommandClassLoader())
                .addCommand("load jar", "[\\h+\\w]*", (param) -> {
                    System.out.println("load jar with argument: " + param);
                })
                .start();

    }
}

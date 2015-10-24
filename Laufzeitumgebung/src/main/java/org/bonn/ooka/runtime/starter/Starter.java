package org.bonn.ooka.runtime.starter;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;

import java.io.IOException;

public class Starter {

    public static void main(String[] args) {
        try {
//            Settings s = Settings.getInstanasce();
//            s.setLogging(false);
//            s.setAnsiConsole(true);
//            s.setReadInputrc(false);
            RuntimeEnvironment runtime = new RuntimeEnvironment();
            runtime.addCommand("load class", (param) -> {
                System.out.println("load class. arg: "+param);

                if(param == null || param.length() == 0) {
//                    try {
//                        ConsoleOutput read = runtime.read("path to class: ");
//                        System.out.println(read.getBuffer());
//                    } catch (IOException e) {
//                        System.out.println("error: "+e.getMessage());
//                        e.printStackTrace();
//                    }
                }
            })
            .addCommand("load jar", (arg) -> {
                System.out.println("load jar with argument: " + arg);
            })
            .addCommand("quit", (arg) -> {
                System.exit(0);
            })
            .start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

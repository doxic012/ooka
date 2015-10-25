package org.bonn.ooka.runtime.starter;

import org.bonn.ooka.runtime.annotation.StartMethod;
import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.environment.RuntimeEnvironment.Arguments;
import org.bonn.ooka.runtime.util.Command;
import org.bonn.ooka.runtime.util.entity.CommandLoadClass;

public class Starter {

    @StartMethod
    public static void main(String[] args) {

        // C:/Projects/ooka/BuchungsSystem/target/classes/org/bonn/ooka/buchung/system/entity/Hotel.class
//        String p = Arguments.DEFAULT_ARGS; //String.format("%s(,\\h*%s)*", Arguments.WORD_OR_QUOTED("\\w+"), Arguments.WORD_OR_QUOTED("\\w+"));
//        String t = " asdst-_c.class";
//        String t2 = String.format(" \"%s\"", t);
//        String t3 = String.format("%s,%s,%s", t2, t2, t);
//        String t4 = "";
//        System.out.println(p);
//        System.out.println(String.format("%s -> %s", t, t.replaceAll(p, "_")));
//        System.out.println(String.format("%s -> %s", t2, t2.replaceAll(p, "_")));
//        System.out.println(String.format("%s -> %s", t3, t3.replaceAll(p, "_")));
//        System.out.println(String.format("%s -> %s", t4, t4.replaceAll(p, "_")));

        Command cmd = new CommandLoadClass();
        cmd.execute("C:/Projects/ooka/BuchungsSystem/target/classes/org/bonn/ooka/buchung/system/entity/Hotel.class");

        RuntimeEnvironment runtime = new RuntimeEnvironment();

        runtime.addCommand("load class", Arguments.MODIFIED_ARGS("class"), new CommandLoadClass())
                .addCommand("load jar", Arguments.MODIFIED_ARGS("jar"), (param) -> {
                    System.out.println("load jar with argument: " + param);
                })
                .start();
    }
}

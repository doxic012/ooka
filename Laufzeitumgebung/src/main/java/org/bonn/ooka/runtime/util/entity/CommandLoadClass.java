package org.bonn.ooka.runtime.util.entity;

import org.bonn.ooka.runtime.util.Command;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLoadClass implements Command<String> {
    private static String COMMA_SPLIT = "\\h*,(?=([^\"]*\"[^\"]*\")*[^\"]*$)\\h*";

    @Override
    public void execute(String classPath) {
        if (classPath == null || classPath.length() == 0) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Path to class:");

            classPath = scan.nextLine();
            System.out.println("param input: " + classPath);
        }

//        String[] args = classPath.split(COMMA_SPLIT);
//        for(String s : args)
//            System.out.println(s);
        String[] args = classPath.split(COMMA_SPLIT);
        URL[] urls = new URL[args.length];
        try {
            // split by comma outside of quotes
            for(int i=0; i<args.length;i++)
                urls[i] = new URL("file://" + args[i]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader loader = new URLClassLoader(urls);
    }
}

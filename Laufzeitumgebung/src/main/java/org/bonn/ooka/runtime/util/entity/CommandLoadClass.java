package org.bonn.ooka.runtime.util.entity;

import org.bonn.ooka.runtime.util.Command;

import java.io.File;
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
        File[] files = new File[args.length];
        try {
            // split by comma outside of quotes
            for (int i = 0; i < args.length; i++) {
                urls[i] = new URL("file://" + args[i]);
                files[i] = new File(args[i]);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader loader = new URLClassLoader(urls);
        System.out.println(loader);

        for (File url : files) {
            try {
                String className = url.getName();
                System.out.println(className);
//                Class<?> loadedClass = loader.loadClass(className);
            } catch (Exception ex) {
                System.out.println("class not found: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}

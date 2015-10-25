package org.bonn.ooka.runtime.util.entity;

import org.bonn.ooka.runtime.util.Command;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandClassLoader implements Command<String> {
    @Override
    public void execute(String classPath) {

        System.out.println("load class. arg: " + classPath);

        if (classPath == null || classPath.length() == 0) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Path to class:");
            classPath = scan.nextLine();
            System.out.println("param input: " + classPath);
        }

        List<URL> urls = new ArrayList<>();
        try {
            for (String path : classPath.split(" ")) {
                urls.add(new URL("file://" + path));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader loader = new URLClassLoader((URL[]) urls.toArray());
    }
}

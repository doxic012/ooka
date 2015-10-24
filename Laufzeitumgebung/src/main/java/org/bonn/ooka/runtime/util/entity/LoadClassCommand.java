package org.bonn.ooka.runtime.util.entity;

import org.bonn.ooka.runtime.util.Command;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class LoadClassCommand implements Command<String> {
    @Override
    public void execute(String classPath){
        List<URL> urls = new ArrayList<>();
        try {
            for(String path : classPath.split(" ")) {
                urls.add(new URL("file://"+path));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader loader = new URLClassLoader((URL[]) urls.toArray());
    }
}

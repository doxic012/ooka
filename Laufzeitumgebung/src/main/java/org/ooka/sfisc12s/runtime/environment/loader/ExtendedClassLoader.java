package org.ooka.sfisc12s.runtime.environment.loader;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class ExtendedClassLoader extends URLClassLoader {

    public ExtendedClassLoader() {
        super(new URL[]{});
    }
    public ExtendedClassLoader(ClassLoader parent) {
        this(new URL[]{}, parent);
    }

    public ExtendedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ExtendedClassLoader(URL[] urls) {
        super(urls);
    }

    public ExtendedClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public void addUrl(URL url) {
        super.addURL(url);
    }

    public void addUrl(URL[] urls) {
        for (URL url : urls)
            addURL(url);
    }
}

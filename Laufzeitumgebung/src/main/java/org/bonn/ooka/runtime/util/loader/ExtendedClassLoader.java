package org.bonn.ooka.runtime.util.loader;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Created by Stefan on 26.10.2015.
 */
public class ExtendedClassLoader extends URLClassLoader {

    public ExtendedClassLoader() {
        super(new URL[]{});
    }

    public ExtendedClassLoader(URL[] urls, ClassLoader parent) throws URISyntaxException {
        super(urls, parent);
    }

    public ExtendedClassLoader(URL[] urls) {
        super(urls);
    }

    public ExtendedClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public void addUrl(URL url) throws URISyntaxException {
        super.addURL(url);
    }

    public void addUrl(URL[] urls) throws URISyntaxException {
        for (URL url : urls)
            addURL(url);
    }
}

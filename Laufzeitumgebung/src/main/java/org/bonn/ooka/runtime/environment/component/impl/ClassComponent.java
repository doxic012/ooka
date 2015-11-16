package org.bonn.ooka.runtime.environment.component.impl;

import org.bonn.ooka.runtime.environment.RuntimeEnvironment;
import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.util.Logger.Impl.LoggerFactory;
import org.bonn.ooka.runtime.util.Logger.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Stefan on 26.10.2015.
 */
public class ClassComponent extends Component {

    private static Logger log = LoggerFactory.getRuntimeLogger(ClassComponent.class);

    public ClassComponent(URL path, String name) {
        super(path, name);
    }

    @Override
    public Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        Class<?> loadedClass = getClassLoader().loadClass(getName());
        setComponentClass(loadedClass);
        setComponentInstance(getComponentClass());
        injectLogger();
        return this;
    }
}


package org.ooka.sfisc12s.runtime.environment.component.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

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
        log.debug("Initializing component (%s).", getData());

        if (getComponentClass() != null) {
            setComponentInstance(getComponentClass());
            return this;
        }

        Class<?> clazz = getClassLoader().loadClass(getData().getName());
        setComponentClass(clazz);
        setComponentInstance(clazz);
//        getComponentStructure().add(clazz);

        return this;
    }
}


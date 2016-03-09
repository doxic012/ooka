package org.ooka.sfisc12s.runtime.environment.component.impl;

import org.ooka.sfisc12s.runtime.environment.component.dto.ComponentDTO;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.IOException;
import java.net.URL;

@Entity
public class ClassComponent extends Component {

    private static Logger log = LoggerFactory.getRuntimeLogger(ClassComponent.class);

    public ClassComponent() {

    }

    public ClassComponent(String name, String filePath, String fileName, URL url) {
        super(name, filePath, fileName, url, "class");
    }

    @Override
    public Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        log.debug("Initializing component (%s).", this);

        if (getComponentClass() != null) {
            setComponentInstance(getComponentClass());
            return this;
        }

        Class<?> clazz = getClassLoader().loadClass(getName());
        setComponentClass(clazz);
        setComponentInstance(clazz);
//        componentStructure.add(clazz);

        return this;
    }
}


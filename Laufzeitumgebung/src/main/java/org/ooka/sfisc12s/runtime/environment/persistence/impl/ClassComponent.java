package org.ooka.sfisc12s.runtime.environment.persistence.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Entity
@DiscriminatorValue("class")
public class ClassComponent extends Component {

    private static Logger log = LoggerFactory.getRuntimeLogger(ClassComponent.class);

    public ClassComponent() {
        setBaseType("class");
    }

    public ClassComponent(String fileName, URL url) throws IOException {
        super(fileName, url, "class");
    }

    public ClassComponent(String fileName, URL url, Scope scope) {
        super(fileName, url, scope, "class");
    }

    @Override
    public Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {
        log.debug("Initializing component (%s).", this);

        if (getComponentClass() != null) {
            setComponentInstance(getComponentClass());
            return this;
        }

        // For classes only: remove the FileName.class from the URL path (e.g ..\..\FileName.class
        URL classUrl = new URL(getUrl().toString().replace(getFileName() + ".class", ""));
        getClassLoader().addUrl(classUrl);

        Class<?> clazz = getClassLoader().loadClass(getFileName());
        setComponentClass(clazz);
        setComponentInstance(clazz);
        setInitialized(true);

        return this;
    }
}


package org.ooka.sfisc12s.runtime.environment.component.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

@Entity
@DiscriminatorValue("class")
public class ClassComponent extends ComponentBase {

    private static Logger log = LoggerFactory.getRuntimeLogger(ClassComponent.class);

    public ClassComponent() {
        setBaseType("class");

    }
    public ClassComponent(String fileName, URL url, String scope) throws IOException {
        super(fileName, url, scope, "class");
    }

    @Override
    public ComponentBase initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {
        log.debug("Initializing component (%s).", this);

        if (getComponentClass() != null) {
            setComponentInstance(getComponentClass());
            return this;
        }

        try {
            // For classes only: remove the FileName.class from the URL path (e.g ..\..\FileName.class
            URL classUrl = new URL(getUrl().toString().replace(getFileName()+".class", ""));
            getClassLoader().addUrl(classUrl);
        } catch (URISyntaxException e) {
            log.error(e, "Error at adding url to classloader");
        }

        Class<?> clazz = getClassLoader().loadClass(getFileName());
        setComponentClass(clazz);
        setComponentInstance(clazz);
        setInitialized(true);

        return this;
    }
}


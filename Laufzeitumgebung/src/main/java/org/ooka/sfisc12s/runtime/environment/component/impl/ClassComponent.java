package org.ooka.sfisc12s.runtime.environment.component.impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import javax.persistence.Entity;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

@Entity
public class ClassComponent extends ComponentBase {

    private static Logger log = LoggerFactory.getRuntimeLogger(ClassComponent.class);

    public ClassComponent() {
        setBaseType("class");

    }
    public ClassComponent(String name, URL url, String scope) throws IOException {
        super(name, url, scope, "class");
    }

    @Override
    public ComponentBase initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {
        log.debug("Initializing component (%s).", this);

        if (getComponentClass() != null) {
            setComponentInstance(getComponentClass());
            return this;
        }

        try {
            getClassLoader().addUrl(this.getUrl());
        } catch (URISyntaxException e) {
            log.error(e, "Error at adding url to classloader");
        }

        Class<?> clazz = getClassLoader().loadClass(getName());
        setComponentClass(clazz);
        setComponentInstance(clazz);

        return this;
    }
}


package org.ooka.sfisc12s.runtime.environment.component.impl;

import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class ReferenceComponent extends ComponentBase {

    private static Logger log = LoggerFactory.getRuntimeLogger(ReferenceComponent.class);

    public ReferenceComponent() {
        setBaseType("reference");
    }

    public ReferenceComponent(String name, URL url, String scope) throws IOException {
        super(name, url, scope, "reference");
    }

    @Override
    public ComponentBase initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {
        getClassLoader().addUrl(this.getUrl());

        return this;
    }
}

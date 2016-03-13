package org.ooka.sfisc12s.runtime.environment.component.impl;

import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

@Entity
@DiscriminatorValue("reference")
public class ReferenceComponent extends ComponentBase {

    public ReferenceComponent() {
        setBaseType("reference");
    }

    public ReferenceComponent(String fileName, URL url) {
        super(fileName, url, "reference");
    }

    @Override
    public ComponentBase initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {
        getClassLoader().addUrl(this.getUrl());
        setInitialized(true);

        return this;
    }
}

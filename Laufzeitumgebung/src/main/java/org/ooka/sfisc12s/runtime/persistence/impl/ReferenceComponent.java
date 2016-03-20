package org.ooka.sfisc12s.runtime.persistence.impl;

import org.ooka.sfisc12s.runtime.persistence.Component;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Entity
@DiscriminatorValue("reference")
public class ReferenceComponent extends Component {

    public ReferenceComponent() {
        setBaseType("reference");
    }

    public ReferenceComponent(String fileName, URL url) {
        super(fileName, url, "reference");
    }

    public ReferenceComponent(String fileName, URL url, Scope scope) {
        super(fileName, url, scope, "reference");
    }

    @Override
    public Component initialize() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {
        getClassLoader().addUrl(this.getUrl());
        setInitialized(true);

        return this;
    }
}

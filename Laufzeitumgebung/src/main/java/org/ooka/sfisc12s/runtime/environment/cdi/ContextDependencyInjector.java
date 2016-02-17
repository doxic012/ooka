package org.ooka.sfisc12s.runtime.environment.cdi;

import org.ooka.sfisc12s.runtime.environment.component.ComponentMap;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.util.HashMap;

/**
 * Created by steve on 17.02.16.
 */
public interface ContextDependencyInjector {

    ExtendedClassLoader getClassLoader();

    ComponentMap getComponents();

//    void inject();
}


package org.bonn.ooka.runtime.util.component;

import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.state.State;

/**
 * Created by Stefan on 26.10.2015.
 */
public class ClassComponent extends Component {
    public ClassComponent(String name, String path, State state) {
        super(name, path, state);
    }

    public ClassComponent(String name, String path, ExtendedClassLoader classLoader) {
        super(name, path, classLoader);
    }
}


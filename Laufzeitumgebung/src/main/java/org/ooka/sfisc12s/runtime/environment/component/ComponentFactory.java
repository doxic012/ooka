package org.ooka.sfisc12s.runtime.environment.component;

import org.ooka.sfisc12s.runtime.environment.component.dto.ComponentDTO;
import org.ooka.sfisc12s.runtime.environment.component.impl.ClassComponent;
import org.ooka.sfisc12s.runtime.environment.component.impl.JarComponent;

/**
 * Created by steve on 08.03.16.
 */
public final class ComponentFactory {

    public static Component createComponent(ComponentDTO dto) {
        switch (dto.getType()) {
            case "jar":
                return new JarComponent();
            case "class":
                return new ClassComponent();
        }

        return null;
    }
}

package org.bonn.ooka.runtime.util.component;

import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.state.State;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Stefan on 26.10.2015.
 */
public class ClassComponent extends Component {

    Object classInstance;

    public ClassComponent(String name, String path, ExtendedClassLoader classLoader) {
        super(name, path, classLoader);
    }

    public ClassComponent(String name, String path, State state) {
        super(name, path, state);
    }

    public Object getClassInstance() {
        return classInstance;
    }

    private void setClassInstance(Class<?> componentClass) {
        try {
            if (componentClass == null)
                classInstance = null;

            // instantiate only when possible
            int mod = componentClass.getModifiers();
            if (!(Modifier.isAbstract(mod) || Modifier.isInterface(mod) || Modifier.isFinal(mod)))
                classInstance = componentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("ComponentClass '%s' cannot be instantiated.%s", getName(), System.lineSeparator());
        }
    }

    @Override
    public void setComponentClass(Class<?> componentObject) {
        super.setComponentClass(componentObject);
        setClassInstance(componentObject);
    }

    @Override
    public Method getRunnableMethod(Class<? extends Annotation> annotationClass) {
        if (getClassInstance() == null)
            return super.getRunnableMethod(annotationClass);

        for (Method method : getClassInstance().getClass().getMethods())
            if (annotationClass != null && method.isAnnotationPresent(annotationClass))
                return method;

        return null;
    }

    @Override
    public String getStatus() {
        return String.format("Instance: %s, ", classInstance.toString()) + super.getStatus();
    }
}


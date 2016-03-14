package org.ooka.sfisc12s.runtime.util;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {
    public static boolean isClassInstantiable(Class<?> clazz) {
        int mod = clazz.getModifiers();
        return !(!Modifier.isPublic(mod) || Modifier.isAbstract(mod) || Modifier.isInterface(mod));
    }

    public static Object instantiate(Class<?> cls) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        // Create instance of the given class
        Constructor constr = (Constructor) cls.getConstructors()[0];
        List<Object> params = new ArrayList<>();
        boolean accessible = constr.isAccessible();
        for (Class<?> pType : constr.getParameterTypes()) {
            params.add((pType.isPrimitive()) ? ClassUtils.primitiveToWrapper(pType).newInstance() : null);
        }

        constr.setAccessible(true);
        Object instance = constr.newInstance(params.toArray());
        constr.setAccessible(accessible);

        return instance;
    }

}

package org.ooka.sfisc12s.runtime.util;

import java.lang.reflect.Modifier;

/**
 * Created by SFI on 10.03.2016.
 */
public class ClassUtil {
    public static boolean isClassInstantiable(Class<?> clazz) {
        int mod = clazz.getModifiers();
        return !(!Modifier.isPublic(mod) || Modifier.isAbstract(mod) || Modifier.isInterface(mod));
    }

}

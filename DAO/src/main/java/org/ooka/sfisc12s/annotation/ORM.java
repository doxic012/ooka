package org.ooka.sfisc12s.annotation;

import org.ooka.sfisc12s.dao.DAO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by steve on 21.11.15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ORM {
    String name() default "";

    String schema() default "";

    Class<? extends DAO> dao();
}

package org.ooka.sfisc12s.annotation;

import org.ooka.sfisc12s.dao.DAO;

import javax.management.relation.RelationType;
import java.lang.annotation.*;

/**
 * Created by Stefan on 22.11.2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Relation {
    Class<?> foreignType();

    String foreignKey();

    String key();
}


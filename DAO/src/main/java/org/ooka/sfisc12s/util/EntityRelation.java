package org.ooka.sfisc12s.util;

import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve on 18.11.15.
 */

// EntityRelation U <-> V
public interface EntityRelation<U, V> {
    List<U> getRelatedEntities(V entity);
}

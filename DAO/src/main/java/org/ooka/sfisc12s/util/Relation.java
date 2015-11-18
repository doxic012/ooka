package org.ooka.sfisc12s.util;

import java.util.List;

/**
 * Created by steve on 18.11.15.
 */

// Relation U <-> V
public interface Relation<U, V> {

    List<U> findAllByEntity(V entity);
}

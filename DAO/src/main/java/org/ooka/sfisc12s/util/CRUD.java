package org.ooka.sfisc12s.util;

import java.util.List;

/**
 * Created by steve on 18.11.15.
 */
public interface CRUD<T> {
    int create(T item);

    T read(int id);

    boolean update( T item );

    boolean delete( Integer primKey );
}

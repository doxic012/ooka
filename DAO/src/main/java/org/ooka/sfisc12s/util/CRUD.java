package org.ooka.sfisc12s.util;

import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.util.List;

/**
 * Created by steve on 18.11.15.
 */
public interface CRUD<T> {
    void create(T item);

    T read(int id) throws InvalidORMException;

    int update(T item);

    void delete(int id);
}

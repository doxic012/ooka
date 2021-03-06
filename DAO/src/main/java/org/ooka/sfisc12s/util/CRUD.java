package org.ooka.sfisc12s.util;

import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve on 18.11.15.
 */
public interface CRUD<T> {
    T create(T item);

    T read(int id);

    T read(Object... args);

    List<T> readAll(Object... args);

    int update(T item);

    int delete(int id);
}

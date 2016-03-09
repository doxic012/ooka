package org.ooka.sfisc12s.runtime.util;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 18.11.15.
 */
public interface CRUD<T> {
    T create(T item);

    T read(int id);

    T read(Map<String, Object> args);

    List<T> readAll(Map<String, Object> args);

    List<T> readAll();

    T update(T item);

    boolean delete(int id);
}

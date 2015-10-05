package org.bonn.ooka.service;

import java.util.List;

/**
 * Created by steve on 05.10.15.
 */
public interface Caching<T> {
    public void cacheResult(String key, List<T> value);
}

package org.bonn.ooka.service.impl;

import org.bonn.ooka.service.Caching;

import java.util.HashMap;
import java.util.List;

/**
 * Created by steve on 06.10.15.
 */
public class DefaultCaching<T> extends HashMap<String, List<T>> implements Caching<T> {

    @Override
    public void cacheResult(String key, List<T> value) {
        this.put(key, value);
    }

    @Override
    public List<T> getCachedResult(String key) {
        return get(key);
    }
}

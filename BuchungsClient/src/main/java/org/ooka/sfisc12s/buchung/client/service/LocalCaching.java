package org.ooka.sfisc12s.buchung.client.service;

import org.ooka.sfisc12s.buchung.system.service.Caching;

import java.util.HashMap;
import java.util.List;

/**
 * Created by steve on 06.10.15.
 */
public class LocalCaching<T> extends HashMap<String, List<T>> implements Caching<T> {

    @Override
    public void cacheResult(String key, List<T> value) {
        this.put(key, value);
    }

    @Override
    public List<T> getCachedResult(String key) {
        return get(key);
    }
}

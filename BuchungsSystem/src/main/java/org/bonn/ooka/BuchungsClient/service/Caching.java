package org.bonn.ooka.BuchungsClient.service;

import java.util.List;

/**
 * Created by steve on 05.10.15.
 */
public interface Caching<T> {
    void cacheResult(String key, List<T> value);

    List<T> getCachedResult(String key);
}

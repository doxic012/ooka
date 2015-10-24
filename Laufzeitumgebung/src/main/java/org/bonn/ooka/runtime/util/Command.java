package org.bonn.ooka.runtime.util;

import java.io.IOException;

/**
 * Created by Stefan on 24.10.2015.
 */
public interface Command<T> {
    void execute(T param);
}

package org.bonn.ooka.runtime.util;

/**
 * Created by Stefan on 24.10.2015.
 */
public interface Stateful {
    State getState();

    void setState(State state);
}

package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.state.exception.StateMethodException;

/**
 * Created by Stefan on 24.10.2015.
 */
public interface State {
    void start(Object... args) throws StateMethodException;

    void stop() throws StateMethodException;

    void load() throws StateMethodException;

    void unload() throws StateMethodException;
}

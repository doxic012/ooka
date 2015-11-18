package org.ooka.sfisc12s.runtime.environment.component.state;

import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;

/**
 * Created by Stefan on 24.10.2015.
 */
public interface State {
    String getName();

    void start(Object... args) throws StateException;

    void stop(Object... args) throws StateException;

    void load() throws StateException;

    void unload() throws StateException;
}

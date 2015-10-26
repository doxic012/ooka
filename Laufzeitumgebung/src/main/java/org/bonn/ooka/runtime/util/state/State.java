package org.bonn.ooka.runtime.util.state;

import org.bonn.ooka.runtime.util.exception.StateMethodNotAllowedException;

/**
 * Created by Stefan on 24.10.2015.
 */
public interface State {
    void start() throws StateMethodNotAllowedException;

    void stop() throws StateMethodNotAllowedException;

    void load() throws StateMethodNotAllowedException;

    void unload() throws StateMethodNotAllowedException;
}

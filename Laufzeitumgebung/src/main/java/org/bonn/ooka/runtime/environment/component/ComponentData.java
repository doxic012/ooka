package org.bonn.ooka.runtime.environment.component;

import org.bonn.ooka.runtime.environment.component.state.State;

import java.net.URL;

/**
 * Created by steve on 17.11.15.
 */
public class ComponentData {

    public ComponentData(String name, URL path, String id, State state) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.state = state;
    }

    public ComponentData(String name, URL path, State state) {
        this.path = path;
        this.name = name;
        this.state = state;
    }

    private State state;

    private String id;

    private String name;

    private URL path;

    ComponentData setState(State state) {
        this.state = state;
        return this;
    }

    ComponentData setId(String id) {
        this.id = id;
        return this;
    }

    State getState() {
        return state;
    }

    public Class<? extends State> getRawState() {
        return state.getClass();
    }

    public String getId() {
        return id;
    }

    public URL getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s - Zustand: %s, Id: %s, Pfad: %s", getName(), getState().getName(), getId(), getPath().toString());
    }
}

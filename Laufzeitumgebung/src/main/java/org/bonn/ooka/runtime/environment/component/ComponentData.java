package org.bonn.ooka.runtime.environment.component;

import org.bonn.ooka.runtime.environment.component.state.State;

import java.net.URL;

/**
 * Created by steve on 17.11.15.
 */
public class ComponentData {

    public ComponentData(String name, URL path, State state, String id) {
        this.state = state;
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public ComponentData(String name, URL path, State state) {
        this.path = path;
        this.name = name;
        this.state = state;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setState(State state) {
        this.state = state;
    }

    private State state;

    private String id;

    private String name;

    private URL path;

    public Class<? extends State> getRawState() {
        return getState().getClass();
    }

    protected State getState() {
        return state;
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
        return String.format("%s - Zustand: %s, Id: %s, Pfad: %s", getName(), state.getName(), getId(), getPath().toString());
    }
}

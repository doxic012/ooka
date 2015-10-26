package org.bonn.ooka.runtime.util.component;

import org.bonn.ooka.runtime.util.state.State;
import org.bonn.ooka.runtime.util.state.StateUnloaded;

public abstract class Component {

    State state = new StateUnloaded(this);

    String id;

    String name;

    Object instance;

    public Component(String id, String name, Object instance) {
        this.id = id;
        this.name = name;
        this.instance = instance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getStatus() {
        return String.format("Identifikationsnummer: %s, Name: %s, Zustand: %s", id, name, state.toString());
    }
}

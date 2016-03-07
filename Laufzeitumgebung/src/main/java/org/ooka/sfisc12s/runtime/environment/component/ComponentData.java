package org.ooka.sfisc12s.runtime.environment.component;

import org.ooka.sfisc12s.runtime.environment.component.scope.Scope;
import org.ooka.sfisc12s.runtime.environment.component.state.State;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Set<Scope> scopes = new HashSet<>();

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

    ComponentData addScope(Scope scope) {
        this.scopes.add(scope);
        return this;
    }

    ComponentData removeScope(Scope scope) {
        this.scopes.remove(scope);
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

    public Set<Scope> getScopes() { return scopes; }

    @Override
    public String toString() {
        return String.format("%s - Zustand: %s, Id: %s, Pfad: %s", getName(), getState().getName(), getId(), getPath().toString());
    }
}

package org.bonn.ooka.runtime.util.component;

import org.bonn.ooka.runtime.util.exception.StateMethodException;
import org.bonn.ooka.runtime.util.loader.ExtendedClassLoader;
import org.bonn.ooka.runtime.util.state.State;
import org.bonn.ooka.runtime.util.state.StateUnloaded;

public abstract class Component {

    State state;

    String id;

    String path;

    String name;

//    Object instance;

    Class<?> componentClass;

    public Component(String name, String path, ExtendedClassLoader classLoader) {
        this.path = path;
        this.name = name;
        this.state = new StateUnloaded(this, classLoader);
    }
    public Component(String name, String path, State state) {
        this.path = path;
        this.name = name;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }

//    public Object getInstance() {
//        return instance;
//    }
//
//    public void setInstance(Object instance) {
//        this.instance = instance;
//    }

    public String getStatus() {
        return String.format("Id: %s, Pfad: %s, Name: %s, Zustand: %s", id, path, name, state.toString());
    }

    public void start() throws StateMethodException {
        state.start();
    }

    public void stop() throws StateMethodException {
        state.stop();
    }

    public void load() throws StateMethodException {
        state.load();
    }

    public void unload() throws StateMethodException {
        state.unload();
    }
}

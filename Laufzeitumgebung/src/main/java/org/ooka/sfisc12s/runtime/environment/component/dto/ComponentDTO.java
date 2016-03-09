package org.ooka.sfisc12s.runtime.environment.component.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.net.URL;

@Entity
@Table(name = "components", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "name", "type", "path", "scope"}))
public class ComponentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private URL path;

    @Column
    private String scope;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPath(URL path) {
        this.path = path;
    }

    public void setScope(String scopes) {
        this.scope = scopes;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public URL getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public ComponentDTO(String name, URL path, Long id, String scope) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.scope = scope;
    }

    public ComponentDTO(String name, URL path, String type) {
        this.path = path;
        this.name = name;
        this.type = type;
    }

    public ComponentDTO() {
    }

    @Override
    public String toString() {
        return String.format("%s - Id: %s, Pfad: %s", getName(), getId(), getPath().toString());
    }
}

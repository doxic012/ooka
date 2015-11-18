package org.ooka.sfisc12s.buchung.system.entity;

/**
 * Created by steve on 05.10.15.
 */
public class Hotel {
    private String name;

    private String ort;

    private int id;

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", ort='" + ort + '\'' +
                '}';
    }
}



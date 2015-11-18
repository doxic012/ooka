package org.ooka.sfisc12s.dto;

import org.ooka.sfisc12s.util.Relation;
import org.ooka.sfisc12s.util.proxy.VirtualList;

// ORM
public class Customer {
    private int id;
    private String name;
    private String vorname;
    private String ort;

    private VirtualList<Product, Customer, Relation<Product, Customer>> productList;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVorname() {
        return vorname;
    }

    public String getOrt() {
        return ort;
    }
}

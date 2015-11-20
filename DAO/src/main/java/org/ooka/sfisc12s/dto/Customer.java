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

    public Customer(int id, String name, String vorname, String ort, VirtualList<Product, Customer, Relation<Product, Customer>> productList) {
        this.id = id;
        this.name = name;
        this.vorname = vorname;
        this.ort = ort;
        this.productList = productList;
    }
}

package org.ooka.sfisc12s.dto;

import org.ooka.sfisc12s.annotation.Column;
import org.ooka.sfisc12s.annotation.Id;
import org.ooka.sfisc12s.annotation.ORM;
import org.ooka.sfisc12s.util.Relation;
import org.ooka.sfisc12s.util.proxy.VirtualList;

@ORM(schema="DAO")
public class Customer {

    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private String vorname;

    @Column
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

    public VirtualList<Product, Customer, Relation<Product, Customer>> getProductList(Relation<Product, Customer> relation) {
        return productList != null ? productList : new VirtualList<>(this, relation);
    }
}

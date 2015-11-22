package org.ooka.sfisc12s.dto;

import org.ooka.sfisc12s.annotation.*;
import org.ooka.sfisc12s.dao.impl.CustomerDAO;
import org.ooka.sfisc12s.dao.impl.ProductDAO;
import org.ooka.sfisc12s.util.EntityRelation;
import org.ooka.sfisc12s.util.proxy.VirtualList;

@ORM(dao = CustomerDAO.class, schema = "DAO")
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

    @Relation(foreignType = Product.class, foreignKey = "customerId", key = "id")
    private VirtualList<Product, Customer> productList;

    public Customer() {
    }

    public Customer(int id, String name, String vorname, String ort) {
        this.id = id;
        this.name = name;
        this.vorname = vorname;
        this.ort = ort;
    }

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

    public VirtualList<Product, Customer> getProductList() {
        return productList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vorname='" + vorname + '\'' +
                ", ort='" + ort + '\'' +
                ", productList=" + productList +
                '}';
    }
}

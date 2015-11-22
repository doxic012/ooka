package org.ooka.sfisc12s.dto;

import org.ooka.sfisc12s.annotation.Column;
import org.ooka.sfisc12s.annotation.Id;
import org.ooka.sfisc12s.annotation.ORM;
import org.ooka.sfisc12s.dao.impl.ProductDAO;

@ORM(dao = ProductDAO.class, schema = "DAO")
public class Product {

    @Id
    @Column
    private int id;

    @Column
    private String bezeichnung;

    @Column
    private int customerId;

    public Product() {
    }

    public Product(int id, String bezeichnung, int customerId) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", bezeichnung='" + bezeichnung + '\'' +
                ", customerId=" + customerId +
                '}';
    }
}

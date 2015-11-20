package org.ooka.sfisc12s.dto;

// ORM
public class Product {
    private int id;
    private String bezeichnung;

    public int getId() {
        return id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Product(int id, String bezeichnung) {
        this.id = id;
        this.bezeichnung = bezeichnung;
    }
}

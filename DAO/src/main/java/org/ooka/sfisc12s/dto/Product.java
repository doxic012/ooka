package org.ooka.sfisc12s.dto;

import org.ooka.sfisc12s.annotation.Column;
import org.ooka.sfisc12s.annotation.Id;
import org.ooka.sfisc12s.annotation.ORM;

@ORM(schema="DAO")
public class Product {

    @Id
    @Column
    private int id;

    @Column
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

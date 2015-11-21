package org.ooka.sfisc12s.dao.impl;

import org.ooka.sfisc12s.dao.DAO;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;

import java.util.List;
import java.util.Properties;

/**
 * Created by steve on 18.11.15.
 */
public class ProductDAO extends AbstractDAO<Product> implements DAO<Product, Customer> {

    public ProductDAO() {
        super(Product.class);
    }

    @Override
    public void create(Product item) {
    }

    @Override
    public Product read(int id) {
        return null;
    }

    @Override
    public void update(Product item) {

    }

    @Override
    public void delete(int primKey) {

    }

    @Override
    public List<Product> findAllByEntity(Customer entity) {
        return null;
    }

    @Override
    public Product findById(int id) {
        return null;
    }

    @Override
    public Product findByValue(String col, Object value) {
        return null;
    }

    @Override
    protected String getConnectionUrl() {
        return "dumbo.inf.fh-bonn-rhein-sieg.de/sfisc12s";
    }

    @Override
    protected Properties getConnectionProperties() {
        Properties props = new Properties();
        props.setProperty("user", "sfisc12s");
        props.setProperty("password", "sfisc12s");

        return props;
    }

}

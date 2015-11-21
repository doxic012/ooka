package org.ooka.sfisc12s.dao.impl;

import org.ooka.sfisc12s.dao.DAO;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;
import org.ooka.sfisc12s.util.ConnectionManager;
import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created by steve on 18.11.15.
 */
public class CustomerDAO extends AbstractDAO<Customer> implements DAO<Customer, Product> {

    public CustomerDAO() {
        super(Customer.class);
    }

    @Override
    public void create(Customer item) {
    }

    @Override
    public Customer read(int id) throws InvalidORMException {
        return find("id", id).get(0);
    }

    @Override
    public int update(Customer item) {
        return update(item);
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Customer> findAllByEntity(Product entity) {
        return null;
    }

    @Override
    public Customer findById(int id) {
        return null;
    }

    @Override
    public Customer findByValue(String col, Object value) {
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


    public static void main(String... args) {
        CustomerDAO dao = new CustomerDAO();
        List<Customer> c = null;
        try {
            for (Customer cust : dao.find()) {
                System.out.println(cust.getName());

            }

        } catch (InvalidORMException e) {
            e.printStackTrace();
        }
        ConnectionManager.closeConnection(dao.getConnectionUrl());
    }
}

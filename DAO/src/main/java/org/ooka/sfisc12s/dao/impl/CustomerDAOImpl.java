package org.ooka.sfisc12s.dao.impl;

import org.ooka.sfisc12s.dao.CustomerDAO;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;

import java.util.List;

/**
 * Created by steve on 18.11.15.
 */
public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public int create(Customer item) {
        return 0;
    }

    @Override
    public Customer read(int id) {
        return null;
    }

    @Override
    public boolean update(Customer item) {
        return false;
    }

    @Override
    public boolean delete(Integer primKey) {
        return false;
    }

    @Override
    public List<Customer> findAllByEntity(Product entity) {
        return null;
    }

    @Override
    public List<Customer> findCustomerByVorname(String vorname) {
        return null;
    }

    @Override
    public List<Customer> findCustomerByName(String name) {
        return null;
    }

    @Override
    public List<Customer> findCustomerByKey(String key) {
        return null;
    }

    @Override
    public List<Customer> findCustomerByCompany(int company) {
        return null;
    }
}

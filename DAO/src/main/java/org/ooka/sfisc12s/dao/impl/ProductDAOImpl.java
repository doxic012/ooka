package org.ooka.sfisc12s.dao.impl;

import org.ooka.sfisc12s.dao.ProductDAO;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;
import org.ooka.sfisc12s.util.CRUD;
import org.ooka.sfisc12s.util.Relation;

import java.util.List;

/**
 * Created by steve on 18.11.15.
 */
public class ProductDAOImpl implements ProductDAO {

    @Override
    public int create(Product item) {
        return 0;
    }

    @Override
    public Product read(int id) {
        return null;
    }

    @Override
    public boolean update(Product item) {
        return false;
    }

    @Override
    public boolean delete(Integer primKey) {
        return false;
    }

    @Override
    public List<Product> findAllByEntity(Customer entity) {
        return null;
    }
}

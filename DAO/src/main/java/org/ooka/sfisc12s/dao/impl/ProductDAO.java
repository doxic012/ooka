package org.ooka.sfisc12s.dao.impl;

import org.ooka.sfisc12s.dao.AbstractDAO;
import org.ooka.sfisc12s.dao.DAO;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;
import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by steve on 18.11.15.
 */
public class ProductDAO extends AbstractDAO<Product>{

    public ProductDAO() throws InvalidORMException {
        super(Product.class);
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

package org.ooka.sfisc12s.dao.impl;

import org.ooka.sfisc12s.annotation.ORM;
import org.ooka.sfisc12s.annotation.Relation;
import org.ooka.sfisc12s.dao.AbstractDAO;
import org.ooka.sfisc12s.dao.DAO;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;
import org.ooka.sfisc12s.util.ConnectionManager;
import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by steve on 18.11.15.
 */
public class CustomerDAO extends AbstractDAO<Customer> {

    public CustomerDAO() throws InvalidORMException {
        super(Customer.class);
    }

    @Override
    public int update(Customer item) {
        try {
            DAO dao = new ProductDAO();
            List<Product> newProducts = item.getProductList();
            List<Product> currentProducts = dao.readAll("customerId", item.getId());

//            currentProducts.stream().collect(newProducts.stream(), .map(Product::getId)
////                    .collect(Collectors.groupingBy(Product::getId)).values().stream()
//                    .filter(current ->
//                            (newProducts.stream().filter(current)))

            for (Product p : newProducts)
                if (dao.read(p.getId()) != null)
                    dao.update(p);
                else
                    dao.create(p);

            currentProducts.stream().filter(p -> !newProducts.contains(p)).forEach(p -> dao.delete(p.getId()));
        } catch (InvalidORMException e) {
            e.printStackTrace();
        }

        return super.update(item);
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
        try {
            CustomerDAO dao = new CustomerDAO();
            Customer c = dao.read(1);
            List<Product> products = c.getProductList();
            for (Product p : products) {
                System.out.println(p);
            }

            products.add(new Product(10, "Tisch", 1));
            products.add(new Product(11, "Stuhl", 1));

            dao.update(c);

            ConnectionManager.closeConnection(dao.getConnectionUrl());
        } catch (InvalidORMException e) {
            e.printStackTrace();
        }
    }
}

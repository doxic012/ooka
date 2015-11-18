package org.ooka.sfisc12s.dao;

import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;
import org.ooka.sfisc12s.util.CRUD;
import org.ooka.sfisc12s.util.Relation;

import java.util.List;

/**
 * Created by steve on 18.11.15.
 */
public interface CustomerDAO extends CRUD<Customer>, Relation<Customer, Product> {
    List<Customer> findCustomerByVorname ( String vorname);

    List<Customer> findCustomerByName ( String name);

    List<Customer> findCustomerByKey ( String key);

    List<Customer> findCustomerByCompany (int company);
}

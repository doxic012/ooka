package org.ooka.sfisc12s.dao;

import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;
import org.ooka.sfisc12s.util.CRUD;
import org.ooka.sfisc12s.util.Relation;

/**
 * Created by steve on 18.11.15.
 */
public interface ProductDAO extends CRUD<Product>, Relation<Product, Customer> {

}

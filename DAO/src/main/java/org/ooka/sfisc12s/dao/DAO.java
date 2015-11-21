package org.ooka.sfisc12s.dao;

import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.dto.Product;
import org.ooka.sfisc12s.util.CRUD;
import org.ooka.sfisc12s.util.Relation;

import java.util.List;

/**
 * Created by steve on 18.11.15.
 */
public interface DAO<U, V> extends CRUD<U>, Relation<U, V> {
    U findById(int id);

    U findByValue(String col, Object value);
}
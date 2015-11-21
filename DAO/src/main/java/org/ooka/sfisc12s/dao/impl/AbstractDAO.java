package org.ooka.sfisc12s.dao.impl;

import org.omg.CORBA.DynAnyPackage.Invalid;
import org.ooka.sfisc12s.annotation.Column;
import org.ooka.sfisc12s.annotation.Id;
import org.ooka.sfisc12s.annotation.ORM;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.util.ConnectionManager;
import org.ooka.sfisc12s.util.exception.InvalidORMException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by SFI on 20.11.2015.
 */
public abstract class AbstractDAO<T> {

    private Class<? extends T> clazz;

    protected abstract String getConnectionUrl();

    protected abstract Properties getConnectionProperties();

    public AbstractDAO(Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    private String getQuery(String formattedQuery, String... filter) throws InvalidORMException {
        ORM orm = clazz.getAnnotation(ORM.class);

        if (orm == null)
            throw new InvalidORMException("Class does not provide valid ORM-Annotation");

        String schema = !orm.schema().equals("") ? "\"" + orm.schema() + "\"." : "";
        String table = !orm.name().equals("") ? "\"" + orm.schema() + "\"" : "\"" + clazz.getSimpleName() + "\"";
        return String.format(formattedQuery, schema, table, filter);
    }

    /**
     * Generate filter query by array of params, with a length dividable by 2
     * "a" = b, "c" = d, ...
     *
     * @param delimiter The delimiter between each
     * @return
     */
    private String getKeyValueArgs(String delimiter, Object... args) {
        return getKeyValueArgs("", delimiter, args);
    }

    private String getKeyValueArgs(String prefix, String delimiter, Object... args) {
        String kvArgs = "";

        if (args == null || args.length <= 0)
            return "";

        int length = args.length - args.length % 2;
        List<String> argList = new ArrayList<>();
        for (int i = 0; i < length; i += 2)
            argList.add(String.format(" \"%s\"='%s' ", args[i], args[i + 1]));

        return prefix + String.join(delimiter, argList);
    }

    private T generate(ResultSet result) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        ResultSetMetaData rsm = result.getMetaData();
        T instance = clazz.newInstance();

        for (Field f : clazz.getDeclaredFields()) {
            Column col = f.getAnnotation(Column.class);

            if (col == null)
                continue;

            boolean access = f.isAccessible();

            f.setAccessible(true);
            f.set(instance, result.getObject(col.name().equals("") ? f.getName() : col.name()));//, f.getType()));
            f.setAccessible(access);
        }

        return instance;
    }

    protected List<T> find(Object... whereArgs) throws InvalidORMException {

        List<T> list = new ArrayList<>();

        try {
            Connection conn = ConnectionManager.getConnection(getConnectionUrl(), getConnectionProperties());
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(getQuery("SELECT * FROM %s%s %s ", getKeyValueArgs("where", "and", whereArgs)));

            while (rs.next()) {
                list.add(generate(rs));
            }
        } catch (SQLException | IllegalAccessException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int update(T item) throws InvalidORMException {
        List<Object> values = new ArrayList<>();

        try {
            Field id = null;
            for (Field f : clazz.getDeclaredFields()) {
                if (!f.isAnnotationPresent(Column.class))
                    continue;

                if (f.isAnnotationPresent(Id.class)) {
                    id = f;
                    continue;
                }

                values.add(f.getName());
                values.add(f.get(item));
            }

            if (id == null)
                throw new InvalidORMException("Missing identifier column.");

            Connection conn = ConnectionManager.getConnection(getConnectionUrl(), getConnectionProperties());
            Statement st = conn.createStatement();
            return st.executeUpdate(getQuery("UPDATE %s%s SET %s WHERE %s", getKeyValueArgs(",", values), getKeyValueArgs("and", (Object) id.getName(), id.get(item))));
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

package org.ooka.sfisc12s.dao;

import org.ooka.sfisc12s.annotation.*;
import org.ooka.sfisc12s.dto.Customer;
import org.ooka.sfisc12s.util.ConnectionManager;
import org.ooka.sfisc12s.util.exception.InvalidORMException;
import org.ooka.sfisc12s.util.proxy.VirtualList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import static org.ooka.sfisc12s.util.SQL.QueryManager.*;

/**
 * Created by SFI on 20.11.2015.
 */
public abstract class AbstractDAO<T> implements DAO<T> {

    private String schema;

    private String table;

    private Class<? extends T> clazz;

    private HashMap<Class<? extends Annotation>, List<Field>> structure = new HashMap<>();

    public AbstractDAO(Class<? extends T> clazz) throws InvalidORMException {
        this.clazz = clazz;
        if (clazz == null)
            throw new InvalidORMException("No valid class passed to DAO");

        ORM orm = clazz.getAnnotation(ORM.class);
        if (orm == null)
            throw new InvalidORMException("Class does not provide valid ORM-Annotation");

        schema = !orm.schema().equals("") ? "\"" + orm.schema() + "\"." : "";
        table = !orm.name().equals("") ? "\"" + orm.name() + "\"" : "\"" + clazz.getSimpleName() + "\"";

        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class))
                structure.computeIfAbsent(Id.class, list -> new ArrayList<>()).add(f);

            if (f.isAnnotationPresent(Column.class))
                structure.computeIfAbsent(Column.class, list -> new ArrayList<>()).add(f);

            if (f.isAnnotationPresent(Relation.class)) {
                structure.computeIfAbsent(Relation.class, list -> new ArrayList<>()).add(f);
                Relation rel = f.getAnnotation(Relation.class);

                if (!rel.foreignType().isAnnotationPresent(ORM.class))
                    throw new InvalidORMException(String.format("Foreign relation class %s does not provide valid ORM annotation", rel.foreignType().getName()));
            }
        }
    }

    // get id by @Id column
    private Field getIdentifier() throws IllegalAccessException {
        return structure.get(Id.class).get(0);
    }

    protected void insertFieldValue(Field f, Object instance, Object value) throws IllegalAccessException {
        boolean access = f.isAccessible();
        f.setAccessible(true);
        f.set(instance, value);
        f.setAccessible(access);
    }

    protected Object getFieldValue(Field f, Object instance) throws IllegalAccessException {
        boolean access = f.isAccessible();
        f.setAccessible(true);
        Object value = f.get(instance);
        f.setAccessible(access);
        return value;
    }

    private T generateDTO(ResultSet result) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        T instance = clazz.newInstance();

        // Process all Columns
        for (Field f : structure.getOrDefault(Column.class, new ArrayList<>())) {
            Column col = f.getAnnotation(Column.class);
            insertFieldValue(f, instance, result.getObject(col.name().equals("") ? f.getName() : col.name()));
        }

        int id = (int) getFieldValue(getIdentifier(), instance);

        // Process all relations
        for (Field f : structure.getOrDefault(Relation.class, new ArrayList<>())) {
            Relation rel = f.getAnnotation(Relation.class);
            ORM orm = rel.foreignType().getAnnotation(ORM.class);
            DAO dao = orm.dao().newInstance();
            Class<?> relClazz = f.getType();

            if (relClazz.isAssignableFrom(VirtualList.class)) {
                insertFieldValue(f, instance, new VirtualList<>(instance, arg -> dao.readAll(rel.foreignKey(), id)));
            } else if (relClazz.isAssignableFrom(Collection.class)) {
                insertFieldValue(f, instance, dao.readAll(rel.foreignKey(), id));
            } else {
                insertFieldValue(f, instance, dao.read(rel.foreignKey(), id));
            }
        }
        return instance;
    }

    @Override
    public List<T> readAll(Object... whereArgs) {

        List<T> list = new ArrayList<>();

        try {
            Connection conn = ConnectionManager.getConnection(getConnectionUrl(), getConnectionProperties());
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT * FROM %s%s %s ", schema, table, getKeyValueArgs("where", "and", whereArgs)));

            while (rs.next()) {
                list.add(generateDTO(rs));
            }
        } catch (SQLException | IllegalAccessException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public T read(Object... whereArgs) {

        try {
            Connection conn = ConnectionManager.getConnection(getConnectionUrl(), getConnectionProperties());
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT * FROM %s%s %s ", schema, table, getKeyValueArgs("where", "and", whereArgs)));

            while (rs.next()) {
                return generateDTO(rs);
            }
        } catch (SQLException | IllegalAccessException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int update(T item) {
        try {
            Field id = getIdentifier();
            List<Object> values = new ArrayList<>();
            for (Field f : structure.get(Column.class)) {
                if (f.isAnnotationPresent(Id.class))
                    continue;

                values.add(f.getName());
                values.add( getFieldValue(f, item));
            }

            Connection conn = ConnectionManager.getConnection(getConnectionUrl(), getConnectionProperties());
            Statement st = conn.createStatement();
            String query = String.format("UPDATE %s%s SET %s WHERE %s", schema, table, getKeyValueArgs(",", values.toArray()), getKeyValueArgs("and", (Object) id.getName(), getFieldValue(id, item)));
            return st.executeUpdate(query);
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int delete(int id) {
        try {
            Field idField = getIdentifier();
            Connection conn = ConnectionManager.getConnection(getConnectionUrl(), getConnectionProperties());
            Statement st = conn.createStatement();

            String query = String.format("DELETE FROM %s%s WHERE %s", schema, table, getKeyValueArgs("and", (Object) idField.getName(), id));
            return st.executeUpdate(query);
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public T create(T item) {
        try {
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldValues = new ArrayList<>();
            for (Field f : structure.get(Column.class)) {
                fieldNames.add("\"" + f.getName() + "\"");
                fieldValues.add("'" + getFieldValue(f, item).toString() + "'");
            }

            Connection conn = ConnectionManager.getConnection(getConnectionUrl(), getConnectionProperties());
            Statement st = conn.createStatement();
            String query = String.format("INSERT INTO %s%s (%s) VALUES (%s)", schema, table, String.join(",", fieldNames), String.join(",", fieldValues));
            st.executeUpdate(query);
            return item;
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public T read(int id) {
        try {
            Field idField = getIdentifier();

            return read(idField.getName(), id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract String getConnectionUrl();

    protected abstract Properties getConnectionProperties();
}

package org.ooka.sfisc12s.dao;

import org.ooka.sfisc12s.dto.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by SFI on 20.11.2015.
 */
public abstract class AbstractDAO<T> {
    private Connection conn;
    private String connectionUrl = "jdbc:postgresql://dumbo.inf.fh-bonn-rhein-sieg.de/sfisc12s";

    public boolean isConnectionOpen() throws SQLException {
        return conn != null && !conn.isClosed();
    }

    public void openConnection() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());

            Properties props = new Properties();
            props.setProperty("user", "sfisc12s");
            props.setProperty("password", "sfisc12s");

            conn = DriverManager.getConnection(connectionUrl, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Generate filter query by array of params, with a length dividable by 2
     * a = b, c = d, ...
     *
     * @param filter The filter arguments
     * @return
     */
    private String getFilterQuery(Object... filter) {
        String filterQuery = "";

        if (filter != null && filter.length > 0) {
            filterQuery = " where ";
            for (int i = 0; i < filter.length - filter.length % 2; i += 2)
                filterQuery += String.format("\"%s\"='%s'", filter[i], filter[i + 1]);
        }
        return filterQuery;
    }

    public ResultSet findObjects(String table, Object... filter) throws SQLException {
        if (!isConnectionOpen())
            return null;

        String query = String.format("SELECT * FROM \"DAO\".\"%s\"%s", table, getFilterQuery(filter));
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;
    }

    // TODO: Reflection?
//    public boolean update
}

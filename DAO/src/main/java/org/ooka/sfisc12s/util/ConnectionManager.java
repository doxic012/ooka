package org.ooka.sfisc12s.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by steve on 21.11.15.
 */
public class ConnectionManager {
    private static HashMap<String, Connection> connectionMap = new HashMap<>();

    private static String connectionPrefix = "jdbc:postgresql://";

    //dumbo.inf.fh-bonn-rhein-sieg.de/sfisc12s

    /*
                    Properties props = new Properties();
                    props.setProperty("user", "sfisc12s");
                    props.setProperty("password", "sfisc12s");*/
    public static Connection getConnection(String url, Properties props) throws SQLException {
        return connectionMap.compute(url, (u, c) -> {
            try {
                if (c != null && !c.isClosed())
                    return c;

                DriverManager.registerDriver(new org.postgresql.Driver());
                return DriverManager.getConnection(connectionPrefix + url, props);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static void closeConnection(String url) {
        connectionMap.computeIfPresent(url, (u, c) -> {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}

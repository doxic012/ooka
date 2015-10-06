package org.bonn.ooka.Buchungssystem;


import org.bonn.ooka.entity.Hotel;
import org.bonn.ooka.service.Hotelsuche;
import org.bonn.ooka.service.impl.DefaultCaching;
import org.bonn.ooka.service.impl.DefaultLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class DBAccess {

    public final static int HOTEL = 0;

    public final static int AUTO = 1;

    private String url = "jdbc:postgresql://dumbo.inf.fh-bonn-rhein-sieg.de/demouser";

    private Connection conn;

    public DBAccess() {

    }

    public static void main(String[] args) {
        Hotelsuche suchService = new HotelRetrieval(new DefaultCaching<>(), new DefaultLogger());

        suchService.openSession();
        System.out.println(suchService.getHotelByName("Hotel"));

        suchService.closeSession();
    }

    public void openConnection() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Properties props = new Properties();
        props.setProperty("user", "demouser");
        props.setProperty("password", "demouser");

        try {
            this.conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Hotel> getObjects(int type, String value) {
        Statement st;
        ResultSet rs;
        List<Hotel> result = new ArrayList();
        if (value.equals("*")) {
            value = "";
        }
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM buchungsystem.hotel WHERE buchungsystem.hotel.name ilike " + "\'%" + value + "%\'");
            while (rs.next()) {
                Hotel hotel = new Hotel();
                hotel.setId(rs.getInt(1));
                hotel.setName(rs.getString(2));
                hotel.setOrt(rs.getString(3));

                result.add(hotel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

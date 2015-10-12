package org.bonn.ooka.BuchungsClient.service;

import org.bonn.ooka.entity.Hotel;

import java.util.List;

/**
 * Created by steve on 05.10.15.
 */
public interface Hotelsuche {

    void openSession();

    void closeSession();

    Hotel getHotelByName(String name);

    List<Hotel> getHotelsByName(String name);
}

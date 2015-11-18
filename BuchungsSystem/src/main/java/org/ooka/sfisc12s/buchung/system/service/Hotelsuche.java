package org.ooka.sfisc12s.buchung.system.service;

import org.ooka.sfisc12s.buchung.system.entity.Hotel;

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

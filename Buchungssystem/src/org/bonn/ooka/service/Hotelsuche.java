package org.bonn.ooka.service;

import org.bonn.ooka.entity.Hotel;

/**
 * Created by steve on 05.10.15.
 */
public interface Hotelsuche {

    void openSession();

    void closeSession();

    Hotel getHotelByName(String name);
}

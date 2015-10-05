package org.bonn.ooka.service;

import org.bonn.ooka.entity.Hotel;

/**
 * Created by steve on 05.10.15.
 */
public interface Hotelsuche {
    public void openSession();

    public Hotel getHotelByName(String name);
}

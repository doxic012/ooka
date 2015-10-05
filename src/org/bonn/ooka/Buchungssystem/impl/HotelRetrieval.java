package org.bonn.ooka.Buchungssystem.impl;

import org.bonn.ooka.entity.Hotel;
import org.bonn.ooka.service.Caching;
import org.bonn.ooka.service.Hotelsuche;

/**
 * Created by steve on 05.10.15.
 */
public class HotelRetrieval implements Hotelsuche {
    private Caching caching;

    @Override
    public void openSession() {

    }

    @Override
    public Hotel getHotelByName(String name) {
        return new Hotel(name);
    }

    public void setCaching(Caching caching) {
        this.caching = caching;
    }
}

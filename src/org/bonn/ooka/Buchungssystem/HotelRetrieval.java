package org.bonn.ooka.Buchungssystem;

import org.bonn.ooka.Hotel.Hotel;
import org.bonn.ooka.Hotel.Hotelsuche;

/**
 * Created by steve on 05.10.15.
 */
public class HotelRetrieval implements Hotelsuche {
    @Override
    public void openSession() {

    }

    @Override
    public Hotel getHotelByName(String name) {
        return new Hotel(name);
    }
}

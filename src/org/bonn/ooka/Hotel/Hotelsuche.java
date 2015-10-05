package org.bonn.ooka.Hotel;

/**
 * Created by steve on 05.10.15.
 */
public interface Hotelsuche {
    public void openSession();

    public Hotel getHotelByName(String name);
}

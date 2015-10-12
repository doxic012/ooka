package org.bonn.ooka.Buchungssystem;

import org.bonn.ooka.entity.Hotel;
import org.bonn.ooka.service.Caching;
import org.bonn.ooka.service.Hotelsuche;

import java.util.List;

/**
 * Created by steve on 05.10.15.
 */
public class HotelRetrieval implements Hotelsuche {

    // Implementation des Cachings mittels "Strategy"-Pattern
    private Caching<Hotel> caching;

    private DBAccess dbAccount;

    protected HotelRetrieval() {

    }

    protected HotelRetrieval(Caching<Hotel> caching) {
        this.caching = caching;
    }

    @Override
    public void openSession() {

        // close opened Session
        if (dbAccount != null)
            closeSession();

        dbAccount = new DBAccess();
        dbAccount.openConnection();
    }

    @Override
    public void closeSession() {
        if (dbAccount == null)
            return;

        dbAccount.closeConnection();
    }

    @Override
    public Hotel getHotelByName(String name) {
        List<Hotel> result = getHotelsByName(name);

        return result.get(0);
    }

    @Override
    public List<Hotel> getHotelsByName(String name) {
        List<Hotel> result = null;

        result = caching.getCachedResult(name);

        if (result == null) {
            result = dbAccount.getObjects(DBAccess.HOTEL, name);

             caching.cacheResult(name, result);
        }

        return result;
    }
}

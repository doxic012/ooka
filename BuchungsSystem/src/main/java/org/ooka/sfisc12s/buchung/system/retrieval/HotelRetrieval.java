package org.ooka.sfisc12s.buchung.system.retrieval;

import org.ooka.sfisc12s.buchung.system.entity.Hotel;
import org.ooka.sfisc12s.buchung.system.service.Caching;
import org.ooka.sfisc12s.buchung.system.service.Hotelsuche;

import java.util.List;

/**
 * Created by steve on 05.10.15.
 */
class HotelRetrieval implements Hotelsuche {

    // Implementation des Cachings mittels "Strategy"-Pattern
    private Caching<Hotel> caching;

    private DBAccess dbAccount;

    HotelRetrieval() {

    }

    HotelRetrieval(Caching<Hotel> caching) {
        this.caching = caching;
    }

    @Override
    public boolean hasCache() {
        return caching != null;
    }

    @Override
    public void setCaching(Caching<Hotel> caching) {
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
        List<Hotel> result = caching.getCachedResult(name);

        if (result == null) {
            result = dbAccount.getObjects(DBAccess.HOTEL, name);

            caching.cacheResult(name, result);
        }

        return result;
    }
}

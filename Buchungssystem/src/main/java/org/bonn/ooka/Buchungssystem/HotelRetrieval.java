package org.bonn.ooka.Buchungssystem;

import org.bonn.ooka.entity.Hotel;
import org.bonn.ooka.service.Caching;
import org.bonn.ooka.service.Logger;
import org.bonn.ooka.service.Hotelsuche;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by steve on 05.10.15.
 */
public class HotelRetrieval implements Hotelsuche {

    // Implementation des Cachings mittels "Strategy"-Pattern
    private Caching<Hotel> caching;

    private Logger log;

    private DBAccess dbAccount;

    public HotelRetrieval() {

    }

    public HotelRetrieval(Caching<Hotel> caching, Logger log) {
        this.caching = caching;
        this.log = log;
    }

    @Override
    public void openSession() {

        // close opened Session
        if (dbAccount != null)
            closeSession();

        log.debug("Opening session");

        dbAccount = new DBAccess();
        dbAccount.openConnection();
    }

    @Override
    public void closeSession() {
        if (dbAccount == null)
            return;

        log.debug("Closing session");
        dbAccount.closeConnection();
    }

    @Override
    public Hotel getHotelByName(String name) {

        if (log != null)
            log.debug(String.format("%s: Zugriff auf Buchungssystem über Methode getHotelByName. Suchwort:%s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), name));

        List<Hotel> result = null;

        if (caching != null)
            result = caching.getCachedResult(name);

        if (result == null) {
            result = dbAccount.getObjects(DBAccess.HOTEL, name);

            if (caching != null)
                caching.cacheResult(name, result);
        }

        return result.size() > 0 ? result.get(0) : null;
    }
}

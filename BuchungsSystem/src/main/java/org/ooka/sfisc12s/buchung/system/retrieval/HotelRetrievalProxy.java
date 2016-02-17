package org.ooka.sfisc12s.buchung.system.retrieval;

import org.ooka.sfisc12s.buchung.system.entity.Hotel;
import org.ooka.sfisc12s.buchung.system.service.Caching;
import org.ooka.sfisc12s.buchung.system.service.Hotelsuche;
import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by steve on 05.10.15.
 */
public class HotelRetrievalProxy implements Hotelsuche {

    @Inject
    private Logger log;

    private HotelRetrieval hotelRetrieval;

    HotelRetrievalProxy() {
        this.hotelRetrieval = new HotelRetrieval();
    }

    HotelRetrievalProxy(Caching<Hotel> caching, Logger log) {
        this.log = log;
        this.hotelRetrieval = new HotelRetrieval(caching);
    }

    @Override
    public void openSession() {
        log.debug("Opening Session for Hotelsuche");
        hotelRetrieval.openSession();
    }

    @Override
    public void closeSession() {
        log.debug("Closing Session for Hotelsuche");
        hotelRetrieval.closeSession();
    }

    @Override
    public Hotel getHotelByName(String name) {
        try {
            log.debug(String.format("%s: Zugriff auf Buchungssystem über Methode getHotelByName. Suchwort:%s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), name));

            return hotelRetrieval.getHotelByName(name);
        } catch (Exception ex) {
            log.debug(String.format("An exception was thrown at getHotelByName: %s", ex.getMessage()));
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Hotel> getHotelsByName(String name) {
        try {
            log.debug(String.format("%s: Zugriff auf Buchungssystem über Methode getHotelsByName. Suchwort:%s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), name));

            return hotelRetrieval.getHotelsByName(name);
        } catch (Exception ex) {
            log.debug(String.format("An exception was thrown at getHotelsByName: %s", ex.getMessage()));
            ex.printStackTrace();
        }

        return null;
    }
    @StartMethod
    public void start() {

    }

    @StopMethod
    public void stop() {

    }
}

package org.ooka.sfisc12s.buchung.system.retrieval;

import org.ooka.sfisc12s.buchung.system.entity.Hotel;
import org.ooka.sfisc12s.buchung.system.service.Caching;
import org.ooka.sfisc12s.buchung.system.service.Hotelsuche;
import org.ooka.sfisc12s.runtime.environment.annotation.*;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Injectable
public class HotelRetrievalProxy implements Hotelsuche {

    private boolean started = false;

    @Inject
    private Logger log;

    private HotelRetrieval hotelRetrieval;

    public HotelRetrievalProxy() {
        this.hotelRetrieval = new HotelRetrieval();
    }

    @Override
    public void setCaching(Caching caching) {
        this.hotelRetrieval.setCaching(caching);
    }

    @Override
    public boolean hasCache() {
        return hotelRetrieval.hasCache();
    }

    @Override
    public void openSession() {
        if (!started) {
            log.debug("Error opening session: Component is not started");
            return;
        }

        log.debug("Opening Session for Hotelsuche");
        hotelRetrieval.openSession();
    }

    @Override
    public void closeSession() {
        if (!started) {
            log.debug("Error closing session: Component is not started");
            return;
        }

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
        started = true;

        log.debug("Component started.");
    }

    @StopMethod
    public void stop() {
        started = false;
        hotelRetrieval.closeSession();

        log.debug("Component stopped.");
    }
}

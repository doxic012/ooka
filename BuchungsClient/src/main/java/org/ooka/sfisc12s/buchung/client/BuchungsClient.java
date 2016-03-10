package org.ooka.sfisc12s.buchung.client;

import org.ooka.sfisc12s.buchung.client.service.LocalCaching;
import org.ooka.sfisc12s.buchung.system.entity.Hotel;
import org.ooka.sfisc12s.buchung.system.service.Caching;
import org.ooka.sfisc12s.buchung.system.service.Hotelsuche;
import org.ooka.sfisc12s.runtime.environment.annotation.Reference;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;

import java.util.Scanner;
import java.util.stream.Collectors;

public class BuchungsClient {

    private Caching caching = new LocalCaching<>();
    @Inject
    private Logger log;

    @Inject
    @Reference(name = "HotelRetrievalProxy")
    private Hotelsuche suchService;

    @StartMethod
    public void start() {
        if (suchService == null) {
            log.debug("No valid instance of hotelsuche service was injected");
            return;
        }

        suchService.setCaching(caching);
        suchService.openSession();

        log.debug("Enter Hotel to search for:");
        System.out.print("> ");

        Scanner sc = new Scanner(System.in);
        String hotel = sc.next();

        log.debug("Hotel(s) found: %s", suchService.getHotelsByName(hotel).stream().map(Hotel::toString).collect(Collectors.joining(", ")));

        suchService.closeSession();
    }

    @StopMethod
    public void stop() {
        log.debug("Component stopped.");
    }
}

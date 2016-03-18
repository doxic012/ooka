package org.ooka.sfisc12s.buchung.client;

import org.ooka.sfisc12s.buchung.client.service.LocalCaching;
import org.ooka.sfisc12s.buchung.system.entity.Hotel;
import org.ooka.sfisc12s.buchung.system.service.Hotelsuche;
import org.ooka.sfisc12s.runtime.environment.annotation.*;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.Scanner;
import java.util.stream.Collectors;

public class BuchungsClient {

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

        if(!suchService.hasCache())
        suchService.setCaching(new LocalCaching<>());
        suchService.openSession();

        log.debug("Enter Hotel to search for:");
        System.out.print("> ");

        Scanner sc = new Scanner(System.in);
        String hotel = "hotel";

        log.debug("Hotel(s) found: %s", suchService.getHotelsByName(hotel).stream().map(Hotel::toString).collect(Collectors.joining(", ")));

        suchService.closeSession();
    }

    @StopMethod
    public void stop() {
        log.debug("Component stopped.");
    }

    public void onEvent(@Observes String eventData) {
        log.debug("Event occured in BuchungsClient: %s", eventData);
    }
}

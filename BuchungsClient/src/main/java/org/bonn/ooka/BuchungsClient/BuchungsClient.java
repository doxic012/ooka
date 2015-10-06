package org.bonn.ooka.BuchungsClient;

import org.bonn.ooka.Buchungssystem.HotelRetrieval;
import org.bonn.ooka.service.Hotelsuche;
import org.bonn.ooka.service.impl.DefaultCaching;
import org.bonn.ooka.service.impl.DefaultLogger;

/**
 * Created by steve on 06.10.15.
 */
public class BuchungsClient {

    public static void main(String[] args) {
        Hotelsuche suchService = new HotelRetrieval(new DefaultCaching<>(), new DefaultLogger());

        suchService.openSession();
        System.out.println(suchService.getHotelByName("Hotel"));

        suchService.closeSession();
    }
}

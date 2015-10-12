package org.bonn.ooka.BuchungsClient;

import org.bonn.ooka.Buchungssystem.HotelRetrievalProxy;
import org.bonn.ooka.service.Hotelsuche;
import org.bonn.ooka.service.LocalCaching;
import org.bonn.ooka.service.SystemLogger;

/**
 * Created by steve on 06.10.15.
 */
public class BuchungsClient {

    public static void main(String[] args) {
        Hotelsuche suchService = new HotelRetrievalProxy(new LocalCaching<>(), new SystemLogger());

        suchService.openSession();
        System.out.println(suchService.getHotelsByName("Hotel"));

        suchService.closeSession();
    }
}

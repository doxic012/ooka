package org.bonn.ooka.buchung.client;

import org.bonn.ooka.buchung.client.service.LocalCaching;
import org.bonn.ooka.buchung.client.service.SystemLogger;
import org.bonn.ooka.buchung.system.retrieval.HotelRetrievalProxy;
import org.bonn.ooka.buchung.system.service.Hotelsuche;

public class BuchungsClient {

    public static void main(String[] args) {
        Hotelsuche suchService = new HotelRetrievalProxy(new LocalCaching<>(), new SystemLogger());

        suchService.openSession();
        System.out.println(suchService.getHotelsByName("Hotel"));

        suchService.closeSession();
    }
}

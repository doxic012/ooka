package org.bonn.ooka.buchung.client;

import org.bonn.ooka.buchung.client.service.LocalCaching;
import org.bonn.ooka.buchung.system.retrieval.HotelRetrievalProxy;
import org.bonn.ooka.buchung.system.service.Hotelsuche;
import org.bonn.ooka.runtime.environment.annotation.StopMethod;
import org.bonn.ooka.runtime.util.Logger.Logger;
import org.bonn.ooka.runtime.environment.annotation.Inject;
import org.bonn.ooka.runtime.environment.annotation.StartMethod;

public class BuchungsClient {

    @Inject
    private Logger logger;

    @StartMethod
    public void start() {
        Hotelsuche suchService = new HotelRetrievalProxy(new LocalCaching<>(), logger);

        suchService.openSession();
        logger.debug("Hotels ");

        suchService.closeSession();
    }

    @StopMethod
    public void stop() {

    }
}

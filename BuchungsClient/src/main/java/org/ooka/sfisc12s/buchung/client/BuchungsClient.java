package org.ooka.sfisc12s.buchung.client;

import org.ooka.sfisc12s.buchung.client.service.LocalCaching;
import org.ooka.sfisc12s.buchung.system.retrieval.HotelRetrievalProxy;
import org.ooka.sfisc12s.buchung.system.service.Hotelsuche;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;

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

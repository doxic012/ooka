package org.ooka.sfisc12s.buchung.client;

import org.ooka.sfisc12s.buchung.system.service.Hotelsuche;
import org.ooka.sfisc12s.runtime.environment.annotation.StopMethod;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.annotation.Inject;
import org.ooka.sfisc12s.runtime.environment.annotation.StartMethod;

public class BuchungsClient {

    @Inject
    private Logger logger;

    @Inject
    private Hotelsuche suchService;

    @StartMethod
    public void start() {
        if (suchService == null) {
            logger.debug("No valid instance of hotelsuche service was injected");
            return;
        }

        suchService.openSession();
        logger.debug("Hotels ");

        suchService.closeSession();
    }

    @StopMethod
    public void stop() {

    }
}

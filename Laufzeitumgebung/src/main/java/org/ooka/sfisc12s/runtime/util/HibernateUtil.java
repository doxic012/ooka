package org.ooka.sfisc12s.runtime.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

public final class HibernateUtil {

    private static Logger log = LoggerFactory.getRuntimeLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    static {
        try {
            log.debug("Configuring hibernate session factory");
            Configuration config = new Configuration().configure("hibernate.cfg.xml");
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
            sessionFactory = config.buildSessionFactory(serviceRegistry);

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            log.error(ex, "Initial SessionFactory creation failed.");
            throw new ExceptionInInitializerError(ex);
        }
    }
}


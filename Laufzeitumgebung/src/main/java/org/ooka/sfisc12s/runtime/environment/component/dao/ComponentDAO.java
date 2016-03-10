package org.ooka.sfisc12s.runtime.environment.component.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.util.HibernateUtil;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ComponentDAO {
    private static Logger log = LoggerFactory.getRuntimeLogger(ComponentDAO.class);

    public static boolean exists(ComponentBase item) {

        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createSQLQuery("SELECT Count(*) FROM ComponentBase WHERE checksum=:n AND scope =:sc AND baseType=:t").
                    setParameter("n", item.getChecksum()).
                    setParameter("sc", item.getScope()).
                    setParameter("t", item.getBaseType());

            return query.getFirstResult() > 0;

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return false;
    }

    public static ComponentBase create(ComponentBase item) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.save(item);
            transaction.commit();

            return item;
        } catch (Exception ex) {
            log.error(ex, "Exception thrown at create from database.");
            transaction.rollback();
        }

        return null;
    }

    public static ComponentBase read(int id) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createQuery("FROM ComponentBase WHERE id=:id").
                    setParameter("id", id);

            return (ComponentBase) query.uniqueResult();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return null;
    }

    public static ComponentBase read(ComponentBase item) {
        return read(item.getChecksum(), item.getScope(), item.getBaseType());
    }

    public static ComponentBase read(String checksum, String scope, String baseType) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createQuery("FROM ComponentBase WHERE checksum=:n AND scope=:sc").
                    setParameter("n", checksum).
                    setParameter("sc", scope).
                    setParameter("t", baseType);

            return (ComponentBase) query.uniqueResult();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return null;
    }


    public static List<ComponentBase> readAll(String checksum, String scope, String baseType) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createQuery("FROM ComponentBase WHERE checksum=:n AND scope=:sc AND baseType=:t").
                    setParameter("n", checksum).
                    setParameter("sc", scope).
                    setParameter("t", baseType);

            return (List<ComponentBase>) query.list();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return null;
    }

    public static List<ComponentBase> readAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            List<ComponentBase> list = (List<ComponentBase>) session.createCriteria(ComponentBase.class).list();
            transaction.commit();

            return list;
        } catch (Exception ex) {
            log.error(ex, "Exception thrown at readAll from database.");
            transaction.rollback();
        }

        return new ArrayList<>();
    }

    public static ComponentBase update(ComponentBase item) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(item);
            transaction.commit();

            return item;
        } catch (Exception ex) {
            log.error(ex, "Exception thrown at readAll from database.");
            transaction.rollback();
        }
        return null;
    }

    public static boolean delete(ComponentBase item) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.delete(item);
            transaction.commit();

            return true;
        } catch (Exception ex) {
            log.error(ex, "Exception thrown at delete from database.");
            transaction.rollback();
        }

        return false;

    }
    public static boolean delete(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            ComponentBase item = read(id);
            session.delete(item);
            transaction.commit();

            return true;
        } catch (Exception ex) {
            log.error(ex, "Exception thrown at delete from database.");
            transaction.rollback();
        }

        return false;
    }
}

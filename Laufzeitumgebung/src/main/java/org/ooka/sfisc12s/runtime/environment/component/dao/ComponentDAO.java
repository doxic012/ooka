package org.ooka.sfisc12s.runtime.environment.component.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable.Scope;
import org.ooka.sfisc12s.runtime.util.HibernateUtil;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ComponentDAO {
    private static Logger log = LoggerFactory.getRuntimeLogger(ComponentDAO.class);

    public static boolean exists(ComponentBase item) {
        if (item == null)
            return false;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.
                createSQLQuery("SELECT Count(*) FROM ComponentBase WHERE id=:id AND scope =:scope").
                setParameter("id", item.getId()).
                setParameter("scope", item.getScope());

        return query.getFirstResult() > 0;
    }

    public static ComponentBase create(ComponentBase item) {

        if (item == null)
            return null;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        session.save(item);
        transaction.commit();

        return item;
    }

    public static ComponentBase read(long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.
                createQuery("FROM ComponentBase WHERE id=:id").
                setParameter("id", id);

        return (ComponentBase) query.uniqueResult();
    }

    public static ComponentBase read(ComponentBase item) {
        if (item == null)
            return null;

        return read(item.getId(), item.getScope());
    }

    public static ComponentBase read(long id, Scope scope) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.
                createQuery("FROM ComponentBase WHERE id=:id AND scope=:scope").
                setParameter("id", id).
                setParameter("scope", scope);

        return (ComponentBase) query.uniqueResult();
    }


    public static List<ComponentBase> readAll(long id, Scope scope) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.
                createQuery("FROM ComponentBase WHERE id=:id AND scope=:scope").
                setParameter("id", id).
                setParameter("scope", scope);

        return (List<ComponentBase>) query.list();
    }

    public static List<ComponentBase> readAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        List<ComponentBase> list = (List<ComponentBase>) session.createCriteria(ComponentBase.class).list();
        transaction.commit();

        return list;
    }

    public static ComponentBase update(ComponentBase item) {
        if (item == null)
            return null;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(item);
        transaction.commit();

        return item;
    }

    public static boolean delete(ComponentBase item) {
        if (item == null)
            return false;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        session.delete(item);
        transaction.commit();

        return true;
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

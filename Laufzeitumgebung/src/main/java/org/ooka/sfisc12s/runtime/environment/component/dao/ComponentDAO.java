package org.ooka.sfisc12s.runtime.environment.component.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.ooka.sfisc12s.runtime.environment.component.Component;
import org.ooka.sfisc12s.runtime.util.HibernateUtil;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.List;
import java.util.function.Consumer;

public class ComponentDAO {
    private static Logger log = LoggerFactory.getRuntimeLogger(ComponentDAO.class);

    public boolean exists(Component c) {

        return false;
    }

    public Component create(Consumer<Component> creator) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
//            creator.accept(item);
            Component item = null;
            session.save(item);
            transaction.commit();

            return item;
        } catch (Exception ex) {
            log.error(ex, "Exception thrown at create from database.");
            transaction.rollback();
        }

        return null;
    }

    public Component read(int id) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createQuery("FROM Component WHERE id=:id").
                    setParameter("id", id);

            return (Component) query.uniqueResult();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return null;
    }

    public Component read(String name, String path, String type) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createQuery("FROM Component WHERE name=:n AND path=:p AND componentType=:t").
                    setParameter("n", name).
                    setParameter("p", path).
                    setParameter("t", type);

            return (Component) query.uniqueResult();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return null;
    }


    public List<Component> readAll(String name, String path, String type) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createQuery("FROM Component WHERE name=:n AND path=:p AND componentType=:t").
                    setParameter("n", name).
                    setParameter("p", path).
                    setParameter("t", type);

            return (List<Component>) query.list();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return null;
    }

    public List<Component> readAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {

            List<Component> list = (List<Component>) session.createCriteria(Component.class).list();
            transaction.commit();
            return list;
        } catch (Exception ex) {
            log.error(ex, "Exception thrown at readAll from database.");
            transaction.rollback();
        }

        return null;
    }

    public Component update(Component item) {
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

    public boolean delete(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Component item = this.read(id);
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

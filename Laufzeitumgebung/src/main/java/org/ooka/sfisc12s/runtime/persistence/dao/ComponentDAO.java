package org.ooka.sfisc12s.runtime.persistence.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.ooka.sfisc12s.runtime.persistence.Component;
import org.ooka.sfisc12s.runtime.util.HibernateUtil;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.List;

public class ComponentDAO {
    private static Logger log = LoggerFactory.getRuntimeLogger(ComponentDAO.class);

    public static Component create(Component item) {

        if (item == null)
            return null;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        session.save(item);
        transaction.commit();

        return item;
    }

    public static Component read(long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.
                createQuery("FROM ComponentBase WHERE id=:id").
                setParameter("id", id);

        return (Component) query.uniqueResult();
    }

    public static List<Component> readAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        List<Component> list = (List<Component>) session.createCriteria(Component.class).list();
        transaction.commit();

        return list;
    }

    public static Component update(Component item) {
        if (item == null)
            return null;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(item);
        transaction.commit();

        return item;
    }

    public static boolean delete(Component item) {
        if (item == null)
            return false;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        session.delete(item);
        transaction.commit();

        return true;
    }

}

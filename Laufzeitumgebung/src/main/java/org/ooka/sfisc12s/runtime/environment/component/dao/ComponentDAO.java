package org.ooka.sfisc12s.runtime.environment.component.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.ooka.sfisc12s.runtime.environment.component.dto.ComponentDTO;
import org.ooka.sfisc12s.runtime.util.CRUD;
import org.ooka.sfisc12s.runtime.util.HibernateUtil;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.List;
import java.util.Map;

public class ComponentDAO implements CRUD<ComponentDTO> {
    private static Logger log = LoggerFactory.getRuntimeLogger(ComponentDAO.class);

    @Override
    public ComponentDTO create(ComponentDTO item) {
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

    @Override
    public ComponentDTO read(int id) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query query = session.
                    createQuery("FROM ComponentDTO WHERE id=:id").
                    setParameter("id", id);

            return (ComponentDTO) query.uniqueResult();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at read from database.");
        }

        return null;
    }

    @Override
    public ComponentDTO read(Map<String, Object> args) {
        return null;
    }

    @Override
    public List<ComponentDTO> readAll(Map<String, Object> args) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            StringBuilder sb = new StringBuilder();
            sb.append("FROM ComponentDTO WHERE ");
            args.entrySet().stream().forEach((e)-> sb.append(String.format("%s='%s'", e.getKey(), e.getValue())));

            Query query = session.createSQLQuery(sb.toString());
            return (List<ComponentDTO>) query.list();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at readAll from database.");
        }

        return null;
    }

    @Override
    public List<ComponentDTO> readAll() {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            return (List<ComponentDTO>) session.createCriteria(ComponentDTO.class).list();

        } catch (Exception ex) {
            log.error(ex, "Exception thrown at readAll from database.");
        }

        return null;
    }

    @Override
    public ComponentDTO update(ComponentDTO item) {
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

    @Override
    public boolean delete(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            ComponentDTO item = this.read(id);
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

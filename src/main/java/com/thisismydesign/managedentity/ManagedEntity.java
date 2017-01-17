package com.thisismydesign.managedentity;

import com.thisismydesign.managedentity.db.DBAdapter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import java.util.HashMap;
import java.util.logging.Logger;

import static org.hibernate.criterion.Restrictions.eq;

public abstract class ManagedEntity {

    private final static Logger LOGGER = Logger.getLogger(ManagedEntity.class.getName());

    public ManagedEntity merge() {
        Session session = DBAdapter.getInstance().getSession();

        session.beginTransaction();
        ManagedEntity object = merge(session);
        session.getTransaction().commit();

        return object;
    }

    public ManagedEntity merge(Session session) {
        ManagedEntity object = this;

        ManagedEntity found = object.findUnique(session);
        if (found != null) {
            object = found;
        } else {
            try {
                object = (ManagedEntity) session.merge(object);
            } catch (ConstraintViolationException e) {
                LOGGER.warning(String.format("While trying to persist object another object with same constraints was" +
                        " persisted by another thread. This should not cause any problems however it will invalidate " +
                        "session (alongside with cache) so for performance reasons it should be avoided. Support for " +
                        "nested sessions would solve this. Object: %s", object));
                session.getTransaction().rollback();

                session = DBAdapter.getInstance().getSession();
                session.beginTransaction();
                found = object.findUnique(session);
                if (found == null) {
                    LOGGER.severe(String.format("ConstraintViolationException while trying to save %s but no such " +
                            "persisted entry is found. We wrongly assumed that another thread cannot delete entries. " +
                            "A recursive approach would solve this issue.", object));
                }
                object = found;
            }
        }

        return object;
    }

    public ManagedEntity update() {
        Session session = DBAdapter.getInstance().getSession();

        session.beginTransaction();
        ManagedEntity object = update(session);
        session.getTransaction().commit();

        return object;
    }

    public ManagedEntity update(Session session) {
        ManagedEntity object = this;
        session.update(object);
        return object;
    }

    protected abstract HashMap<String, ?> getEqualsCriteriaList();

    private ManagedEntity findUnique(Session session) {
        HashMap<String, ?> criteriaList = getEqualsCriteriaList();
        Criteria criteria = session.createCriteria(this.getClass());
        for (String key : criteriaList.keySet()) {
            criteria.add(eq(key, criteriaList.get(key)));
        }
        return (ManagedEntity) criteria.uniqueResult();
    }
}

package com.thisismydesign.managedentity;

import com.thisismydesign.managedentity.db.DBAdapter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import java.util.HashMap;
import java.util.logging.Logger;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * This class supports entity management with secondary DB keys (a.k.a. business keys) while also using Ids as primary
 * keys
 *
 * <p>Entity classes should extend.
 * See example in test package.
 *
 * @see <a href="http://lifeinacubicleblog.com/2016/10/09/jpa-the-uniqueness-dilemma-solution/">JPA - The uniqueness dilemma and solution</a>
 */
public abstract class ManagedEntity {

    private final static Logger LOGGER = Logger.getLogger(ManagedEntity.class.getName());

    /**
     * Persists self if no persisted entity with the same secondary keys exists.
     *
     * <p>{@link Session} is queried from DBAdapter via {@link DBAdapter#getCurrentSession}.
     *
     * @return persisted entity
     */
    public ManagedEntity merge() {
        Session session = DBAdapter.getInstance().getCurrentSession();

        session.beginTransaction();
        ManagedEntity object = merge(session);
        session.getTransaction().commit();

        return object;
    }

    /**
     * Persists self using provided {@link Session} if no persisted entity with the same secondary keys exists.
     *
     * <p>Upon {@link ConstraintViolationException} {@link Session} is queried from DBAdapter via {@link DBAdapter#getCurrentSession}.
     *
     * @param session {@link Session} used to access DB
     * @return persisted entity
     */
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

                session = DBAdapter.getInstance().getCurrentSession();
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

    /**
     * Updates self if no persisted entity with the same secondary keys is already present.
     *
     * <p>{@link Session} is queried from DBAdapter via {@link DBAdapter#getCurrentSession}.
     *
     * @return persisted entity
     */
    public ManagedEntity update() {
        Session session = DBAdapter.getInstance().getCurrentSession();

        session.beginTransaction();
        ManagedEntity object = update(session);
        session.getTransaction().commit();

        return object;
    }

    /**
     * Updates self using provided {@link Session} if no persisted entity with the same secondary keys exists.
     *
     * @param session {@link Session} used to access DB
     * @return persisted entity
     */
    public ManagedEntity update(Session session) {
        ManagedEntity object = this;
        session.update(object);
        return object;
    }

    /**
     * Get uniqueness criteria of self in a HashMap using Java attribute name as key.
     *
     * <p>All child entities must implement it.
     * <p>Example implementation:
     * <pre>
     * <code>
     *{@literal @}Column(name = "name")
     * private String name;
     *
     *{@literal @}Column(name = "group_name")
     * private String groupName;
     *
     * ...
     *{@code
     * protected HashMap<String, ?> getEqualsCriteriaList() {
     *     HashMap<String, String> criteriaList = new HashMap<>();
     *     criteriaList.put("name", getName());
     *     criteriaList.put("groupName", getGroupName());
     *
     *     return criteriaList;
     * }
     * }
     * </code>
     * </pre>
     *
     * @return uniqueness criteria
     */
    protected abstract HashMap<String, ?> getEqualsCriteria();

    private ManagedEntity findUnique(Session session) {
        HashMap<String, ?> criteriaList = getEqualsCriteria();
        Criteria criteria = session.createCriteria(this.getClass());
        for (String key : criteriaList.keySet()) {
            criteria.add(eq(key, criteriaList.get(key)));
        }
        return (ManagedEntity) criteria.uniqueResult();
    }
}

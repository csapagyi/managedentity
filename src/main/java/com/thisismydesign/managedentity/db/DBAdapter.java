package com.thisismydesign.managedentity.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * This class supports getting EntityManager and Session objects from JPA.
 */
public class DBAdapter {

    private static DBAdapter instance = null;

    private EntityManagerFactory emf;
    private SessionFactory sf;

    /**
     * Get {@link DBAdapter} singleton instance that uses {@code persistenceUnitName} to get EntityManager and Session
     * objects from JPA.
     *
     * <p>Needs to be called first to initialize {@code persistenceUnitName}.
     *
     * @param persistenceUnitName name of the persistence unit
     * @return {@link DBAdapter} instance
     */
    public static DBAdapter getInstance(String persistenceUnitName) {
        return getInstanceFromName(persistenceUnitName);
    }

    /**
     * Get {@link DBAdapter} singleton instance that uses previously specified {@code persistenceUnitName}.
     *
     * @return {@link DBAdapter} instance
     * @throws IllegalArgumentException if no {@code persistenceUnitName} was specified previously
     */
    public static DBAdapter getInstance(){
        return getInstanceFromName(null);
    }

    /**
     * Get {@link Session} object using {@link SessionFactory}'s API.
     *
     * @return {@link Session}
     */
    public Session getSession() {
        return sf.openSession();
    }

    /**
     * Get {@link Session} object using {@link SessionFactory}'s API.
     *
     * <p>{@code hibernate.current_session_context_class} property needs to be set in {@code persistence.xml}.
     *
     * @return {@link Session}
     */
    public Session getCurrentSession() {
        return sf.getCurrentSession();
    }

    /**
     * Get {@link EntityManager} object using {@link EntityManagerFactory}'s API.
     *
     * @return {@link EntityManager}
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    private static synchronized DBAdapter getInstanceFromName(String persistenceUnitName) {
        if (instance == null) {
            if (persistenceUnitName == null) {
                throw new IllegalArgumentException("No persistence unit name provided on first run.");
            } else {
                instance = new DBAdapter(persistenceUnitName);
            }
        }

        return instance;
    }

    private DBAdapter(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        sf = emf.unwrap(SessionFactory.class);
    }
}

package com.thisismydesign.managedentity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBAdapter {

    private static DBAdapter instance = null;

    private EntityManagerFactory emf;
    private SessionFactory sf;

    public static DBAdapter getInstance(){
        return getInstanceFromName(null);
    }

    public static DBAdapter getInstance(String persistenceUnitName) {
        return getInstanceFromName(persistenceUnitName);
    }

    public Session getSession() {
        return sf.getCurrentSession();
    }

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

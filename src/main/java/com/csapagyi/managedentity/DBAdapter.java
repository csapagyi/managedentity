package com.csapagyi.managedentity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBAdapter {

    private static DBAdapter instance = null;

    private static final String persistenceUnitName = "myPersistenceUnit";

    private EntityManagerFactory emf;
    private SessionFactory sf;

    public static synchronized DBAdapter getInstance() {
        if(instance == null) {
            instance = new DBAdapter();
        }
        return instance;
    }

    private DBAdapter() {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        sf = emf.unwrap(SessionFactory.class);
    }

    public Session getSession() {
        return sf.getCurrentSession();
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

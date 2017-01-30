package com.thisismydesign.managedentity.dao;

import com.thisismydesign.managedentity.ManagedEntity;

/**
 * This interface supports unified access to DAO classes.
 *
 * <p>DAO classes should implement.
 * See example in test package.
 */
public interface IManagedEntityDAO {
    ManagedEntity persist(ManagedEntity entity);
}

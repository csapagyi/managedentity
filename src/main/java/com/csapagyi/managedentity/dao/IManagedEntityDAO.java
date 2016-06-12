package com.csapagyi.managedentity.dao;

import com.csapagyi.managedentity.ManagedEntity;

public interface IManagedEntityDAO {
    ManagedEntity persist(ManagedEntity entity);
}

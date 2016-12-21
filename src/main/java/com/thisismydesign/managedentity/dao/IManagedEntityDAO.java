package com.thisismydesign.managedentity.dao;

import com.thisismydesign.managedentity.ManagedEntity;

public interface IManagedEntityDAO {
    ManagedEntity persist(ManagedEntity entity);
}

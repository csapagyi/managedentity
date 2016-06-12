package com.csapagyi.managedentity.example.dao;

import com.csapagyi.managedentity.ManagedEntity;
import com.csapagyi.managedentity.dao.IManagedEntityDAO;
import com.csapagyi.managedentity.example.model.City;

public class CityDAO implements IManagedEntityDAO {

    public ManagedEntity persist(ManagedEntity entity) {
        City city = (City) entity;

        return city.merge();
    }
}

package com.thisismydesign.managedentity.example.dao;

import com.thisismydesign.managedentity.ManagedEntity;
import com.thisismydesign.managedentity.dao.IManagedEntityDAO;
import com.thisismydesign.managedentity.example.model.City;

public class CityDAO implements IManagedEntityDAO {

    public ManagedEntity persist(ManagedEntity entity) {
        City city = (City) entity;

        return city.merge();
    }
}

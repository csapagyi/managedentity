package com.csapagyi.managedentity.example.dao;

import com.csapagyi.managedentity.ManagedEntity;
import com.csapagyi.managedentity.dao.IManagedEntityDAO;
import com.csapagyi.managedentity.example.model.City;
import com.csapagyi.managedentity.example.model.Person;

public class PersonDAO implements IManagedEntityDAO {

    private CityDAO cityDAO;

    public PersonDAO() {
        cityDAO = new CityDAO();
    }

    public ManagedEntity persist(ManagedEntity entity) {
        Person person = (Person) entity;

        City city = (City) cityDAO.persist(person.getCity());

        person.setCity(city);

        return person.merge();
    }
}

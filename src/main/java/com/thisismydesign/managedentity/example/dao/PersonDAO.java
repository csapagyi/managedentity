package com.thisismydesign.managedentity.example.dao;

import com.thisismydesign.managedentity.ManagedEntity;
import com.thisismydesign.managedentity.dao.IManagedEntityDAO;
import com.thisismydesign.managedentity.example.model.City;
import com.thisismydesign.managedentity.example.model.Person;

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

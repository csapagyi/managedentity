package com.thisismydesign.managedentity.example.model;

import com.thisismydesign.managedentity.ManagedEntity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

public class City extends ManagedEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "city")
    private List<Person> persons;

    @Override
    protected HashMap<String, ?> getEqualsCriteriaList() {
        HashMap<String, Object> criteriaList = new HashMap<>();
        criteriaList.put("name", getName());

        return criteriaList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}

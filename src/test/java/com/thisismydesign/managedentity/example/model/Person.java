package com.thisismydesign.managedentity.example.model;

import com.thisismydesign.managedentity.ManagedEntity;

import javax.persistence.*;
import java.util.HashMap;

public class Person extends ManagedEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    protected HashMap<String, ?> getEqualsCriteria() {
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}

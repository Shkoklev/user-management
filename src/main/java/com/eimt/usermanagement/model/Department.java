package com.eimt.usermanagement.model;

import javax.persistence.*;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    public Department(){}

    public Department(String name, User manager) {
        this.name = name;
        this.manager = manager;
    }

    public Long getId() {
        return id;
    }

    public void changeId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public User getManager() {
        return manager;
    }

    public void changeManager(User manager) {
        this.manager = manager;
    }
}

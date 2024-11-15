package com.mukesh.smarthealthmanagement.entities;

import com.mukesh.smarthealthmanagement.Role;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN") 
public class Admin extends User {


    public Admin() {
        super();
        getRoles().add(Role.ADMIN);
    }
}

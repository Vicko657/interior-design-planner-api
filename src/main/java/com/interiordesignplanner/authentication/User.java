package com.interiordesignplanner.authentication;

import com.interiordesignplanner.AbstractEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity {

    // User's firstname
    private String firstName;

    // User's lastname
    private String lastName;

    // User's email address
    private String email;

    // User's mobile number
    private String mobileNumber;

    // User's username
    private String username;

    // User's password
    private String password;

    // User's role
    @Enumerated(EnumType.STRING)
    private Roles roles;

}

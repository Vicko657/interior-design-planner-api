package com.interiordesignplanner.designer;

import com.interiordesignplanner.AbstractEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "designers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Designer extends AbstractEntity {

    // Designer's firstname
    private String firstName;

    // Designer's lastname
    private String lastName;

    // Designer's email address
    private String email;

    // Designer's mobile number
    private String mobileNumber;

    // Designer's username
    private String username;

    // Designer's password
    private String password;

}

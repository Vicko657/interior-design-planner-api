package com.interiordesignplanner.designer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.interiordesignplanner.AbstractEntity;
import com.interiordesignplanner.client.Client;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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

    // Creates One to Many Bidirectional relationship with the client entity
    @OneToMany(mappedBy = "designer")
    @JsonManagedReference
    private List<Client> clients = new ArrayList<>();

}

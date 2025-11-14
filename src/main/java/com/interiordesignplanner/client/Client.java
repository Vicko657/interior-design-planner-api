package com.interiordesignplanner.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.interiordesignplanner.AbstractEntity;
import com.interiordesignplanner.project.Project;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Models a Client of the interior designer. Stores the client's
 * contact details and preferences to manage relationships.
 * One client can have multiple projects and is extending the
 * AbstractEntity class, which provides their unique identifier
 * and timestamps for creation and updates to their data.
 */

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client extends AbstractEntity {

    // Client's firstname
    private String firstName;

    // Client's lastname
    private String lastName;

    // Client's email address
    private String email;

    // Client's phone number
    private String phone;

    // Client's full address including streetname, city and postcode
    private String address;

    // Notes on specific preferences for the client
    private String notes;

    // Creates One to Many Bidirectional relationship with the project entity
    @OneToMany(mappedBy = "client")
    @JsonManagedReference
    private List<Project> projects;

    // Constructor
    public Client(String firstName, String lastName, String email, String phone, String address, String notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.notes = notes;
    }

    // Getters
    @Override
    public Long getId() {
        return super.getId();
    }

}

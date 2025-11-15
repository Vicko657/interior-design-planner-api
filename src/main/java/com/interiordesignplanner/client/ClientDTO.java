package com.interiordesignplanner.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    // Client's id
    private Long id;

    // Client's firstname
    private String firstName;

    // Client's lastname
    private String lastName;

    // Client's email address
    private String email;

    // Client's phone number
    private String phone;

    // Client's address
    private String address;

    // Client's notes
    private String notes;

    // How many projects the Client has
    private Long totalProjects;

}

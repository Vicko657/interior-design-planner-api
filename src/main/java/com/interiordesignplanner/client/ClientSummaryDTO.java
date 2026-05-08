package com.interiordesignplanner.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryDTO {

    // Client's id
    private Long id;

    // Client's fullname
    private String fullName;

    // Client's email address
    private String email;

    // Client's phone number
    private String phone;

    // Client's address
    private String address;

    // How many projects the Client has
    private Long totalProjects;

    // Client's notes
    private String notes;

}

package com.interiordesignplanner.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientUpdateDTO {

    // Client's firstname
    private String firstName;

    // Client's lastname
    private String lastName;

    // Client's email address
    @Email
    private String email;

    // Client's phone number
    @Size(min = 11, max = 11)
    private String phone;

    // Client's address
    private String address;

    // Client's notes
    private String notes;

}

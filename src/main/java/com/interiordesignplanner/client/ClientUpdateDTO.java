package com.interiordesignplanner.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 5, max = 15, message = "First name must be between 5 and 15 characters")
    private String firstName;

    // Client's lastname
    @Size(min = 5, max = 15, message = "Last name must be between 5 and 15 characters")
    private String lastName;

    // Client's email address
    @Email(message = "Invaild email address")
    private String email;

    // Client's phone number
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    private String phone;

    // Client's address
    private String address;

    // Client's notes
    @Size(min = 5, max = 200, message = "Notes must be between 5 and 200 characters")
    private String notes;

}

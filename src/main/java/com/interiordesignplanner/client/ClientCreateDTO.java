package com.interiordesignplanner.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class ClientCreateDTO {

    // Client's firstname
    @NotBlank(message = "Client's firstName is required")
    private String firstName;

    // Client's lastname
    @NotBlank(message = "Client's lastName is required")
    private String lastName;

    // Client's email address
    @NotBlank(message = "Client's email is required")
    @Email
    private String email;

    // Client's phone number
    @NotBlank(message = "Client's phoneNumber is required")
    @Size(min = 11, max = 11)
    private String phone;

    // Client's address
    @NotBlank(message = "Client's address is required")
    private String address;

    // Client's notes
    private String notes;

}

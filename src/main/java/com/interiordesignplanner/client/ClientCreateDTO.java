package com.interiordesignplanner.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class ClientCreateDTO {

    // Client's firstname
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    // Client's lastname
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    // Client's email address
    @NotBlank(message = "Email is required")
    @Email(message = "Invaild email address")
    private String email;

    // Client's phone number
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{11}$", message = "Mobile number must be 10 digits")
    private String phone;

    // Client's address
    @NotBlank(message = "Address is required")
    private String address;

    // Client's notes
    @Size(min = 5, max = 200, message = "Notes must be between 5 and 200 characters")
    private String notes;

}

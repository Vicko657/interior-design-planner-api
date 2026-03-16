package com.interiordesignplanner.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCreateDTO {

    // User's firstname
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    // User's lastname
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    // User's email address
    @NotBlank(message = "Email is required")
    @Email(message = "Invaild email address")
    private String email;

    // User's mobile number
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{11}$", message = "Mobile number must be 11 digits")
    private String mobileNumber;

    // User's username
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String username;

    // User's password
    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be more 5 characters")
    private String password;

    // User's role
    @Enumerated(EnumType.STRING)
    private Roles roles;

}

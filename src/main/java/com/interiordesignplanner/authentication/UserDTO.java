package com.interiordesignplanner.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    // User's id
    private Long id;

    // User's firstname
    private String firstName;

    // User's lastname
    private String lastName;

    // User's email address
    private String email;

    // User's mobile number
    private String mobileNumber;

    // User's username
    private String username;

    // User's password
    private String password;

    // User's role
    @Enumerated(EnumType.STRING)
    private Roles roles;
}

package com.interiordesignplanner.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    // User's username
    private String username;

    // User's password
    private String password;

}

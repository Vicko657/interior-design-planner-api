package com.interiordesignplanner.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserLoginResponse {

    // Login response
    private String response;

    // Login jwttoken
    private String jwtToken;

}

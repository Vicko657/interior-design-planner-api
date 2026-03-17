package com.interiordesignplanner.authentication;

import lombok.Data;

@Data
public class UserLoginResponse {

    // Login response
    private String response;

    // Login jwttoken
    private String jwtToken;

}

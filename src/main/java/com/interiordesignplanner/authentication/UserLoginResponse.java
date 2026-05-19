package com.interiordesignplanner.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Response body for user login")
@AllArgsConstructor
@Data
public class UserLoginResponse {

    @Schema(description = "User login response", example = "Successfully logged in")
    private String response;

    @Schema(description = "User JWT token", example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhbm5ld2lsbGlhbXMiLCJpYXQiOjE3NzgyNjc2NzMsImV4cCI6MTc3ODI3MTI3M30.0CIpLq0CDYASdl6RGIus9NomDMqubyOKgVtZx0Db0oNLFNY2Y274KMd9tqPJL4YF")
    private String jwtToken;

}

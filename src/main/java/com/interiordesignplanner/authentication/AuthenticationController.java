package com.interiordesignplanner.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Rest Controller for authenticating users
 * 
 * 
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    // User Service layer
    @Autowired
    public AuthenticationService authenticationService;

    /**
     * POST: Login User
     * 
     * @return a new jwttoken
     * @response 200 if users credentials are valid
     */
    @Tag(name = "users", description = "Information about the users")
    @Operation(summary = "Login User", description = "Allows the user to login and generates a new jwttoken")
    @ApiResponse(responseCode = "200", description = "Successfully logged in")
    @ApiResponse(responseCode = "401", description = "Invalid username or password")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginDTO userLoginDTO) {

        String jwtToken = authenticationService.login(userLoginDTO);
        return ResponseEntity.ok(new UserLoginResponse("Successfully logged in", jwtToken));
    }

    /**
     * POST: Register User
     * 
     * 
     * @return Successful registration message
     * @response 201 if new user was created
     * @response 400 for duplicate users
     */
    @Tag(name = "users", description = "Information about the users")
    @Operation(summary = "Register User", description = "Creates a new User and encodes password")
    @ApiResponse(responseCode = "201", description = "Registration successful")
    @ApiResponse(responseCode = "400", description = "Registration failed")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        authenticationService.registerDesigner(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
    }

}

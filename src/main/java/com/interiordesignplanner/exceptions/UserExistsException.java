package com.interiordesignplanner.exceptions;

public class UserExistsException extends RuntimeException {

    /**
     * Constructs a new UserExistsException when the user entity already exists in
     * the database.
     */

    public UserExistsException(String fieldName, Object value) {
        super("User is exists with " + fieldName + ": " + value);
    }

}

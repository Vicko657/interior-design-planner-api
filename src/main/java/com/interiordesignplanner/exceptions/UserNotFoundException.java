package com.interiordesignplanner.exceptions;

public class UserNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new UserNotFoundException when the user entity with
     * the username is not found.
     */

    public UserNotFoundException(String fieldName, Object value) {
        super("User is not found with " + fieldName + ": " + value);
    }

}

package com.interiordesignplanner.exceptions;

/**
 * Thrown when a client entity is not found in the interior design planner.
 */
public class ClientNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new ClientNotFoundException when the client entity with
     * the clientId and lastName is not found.
     */

    public ClientNotFoundException(String fieldName, Object value) {
        super("Client is not found with " + fieldName + ": " + value);
    }

}

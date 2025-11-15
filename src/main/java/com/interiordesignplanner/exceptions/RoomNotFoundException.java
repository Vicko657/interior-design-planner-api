package com.interiordesignplanner.exceptions;

/**
 * Thrown when a room entity is not found in the interior design planner.
 */
public class RoomNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new RoomNotFoundException when the room entity with
     * the roomId is not found.
     *
     * @param id is not found
     */
    public RoomNotFoundException(String fieldName, Object value) {
        super("Room is not found with " + fieldName + ": " + value);
    }

}

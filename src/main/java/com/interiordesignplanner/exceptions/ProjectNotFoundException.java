package com.interiordesignplanner.exceptions;

/**
 * Thrown when a project entity is not found in the interior design planner.
 */
public class ProjectNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new ProjectNotFoundException when the project entity with
     * the projectId is not found.
     *
     */

    public ProjectNotFoundException(String fieldName, Object value) {
        super("Project is not found with " + fieldName + ": " + value);
    }

}

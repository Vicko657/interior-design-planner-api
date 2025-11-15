package com.interiordesignplanner.project;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Project} entities.
 * 
 * Projection Queries {@link Deadline} were used to select
 * columns the query should return.
 * <p>
 * Provides custom CRUD operations and query methods for accessing project data.
 * </p>
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Finds all projects by specific status.
     * 
     * @param status the specific project status
     * @return an {@link List} of projects associated with the specified status
     */
    List<Project> findProjectsByStatus(ProjectStatus status);

    /**
     * Gets all projects in ascending order of due date.
     * 
     * @return an {@link List} projects associated with the specified status
     */
    @Query("select p.dueDate, p.startDate, p.projectName, p.status, p.client.id, p.room.id from Project p order by p.dueDate ASC")
    List<Deadline> getAllProjectsOrderByDueDate();
}

// Due Date projection query
record Deadline(LocalDate dueDate, LocalDate startDate, String projectName, ProjectStatus status, Long clientId,
        Long roomId) {

}

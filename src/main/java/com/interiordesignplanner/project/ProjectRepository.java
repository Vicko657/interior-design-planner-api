package com.interiordesignplanner.project;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    /**
     * Finds all projects by specific status.
     * 
     * @param status the specific project status
     * @return an {@link List} of projects associated with the specified status
     */
    Page<Project> findProjectsByStatus(ProjectStatus status, Pageable pageable);

    /**
     * Gets total number of clients projects.
     * 
     * @return the number of projects associated with the client
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.client.id = :clientId")
    Long countClientsProjects(Long clientId);

    /**
     * Gets all projects in ascending order of due date.
     * Removes the completed project off the list.
     * 
     * @return an {@link List} projects associated with the specified status
     */
    @Query("SELECT p.projectName,  p.status, p.dueDate FROM Project p WHERE NOT p.status = 'Completed' ORDER BY p.dueDate ASC")
    Page<Deadline> getAllProjectsOrderByDueDate(Pageable pageable);

    /**
     * Finds all clients for the logged in user with pagination.
     *
     * @param userId   the designers unique identification
     * @param pageable pagination info
     * @return paginated list of clients
     */
    @Query("SELECT new com.interiordesignplanner.project.ProjectSummaryDTO(p.id, CONCAT(c.firstName,' ', c.lastName), p.projectName, p.status, p.budget, p.startDate, p.dueDate, p.description) FROM Project p LEFT JOIN p.client c LEFT JOIN c.designer d LEFT JOIN d.user u WHERE c.designer.id = :userId GROUP BY p.id")
    Page<ProjectSummaryDTO> findProjectsByDesignerId(@Param("userId") Long userId, Pageable pageable);

}

// Deadline projection query
record Deadline(String projectName, ProjectStatus status, LocalDate dueDate) {

}

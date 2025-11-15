package com.interiordesignplanner.project;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientService;
import com.interiordesignplanner.exceptions.ProjectNotFoundException;

import jakarta.transaction.Transactional;

/**
 * Project service class handles business logic and operations to help manage
 * the projects.
 * 
 * <p>
 * Responsibilities include creating, updating, retrieving, and deleting project
 * records, enforcing project status (PLANNING, ACTIVE, COMPLETED) to help track
 * progress,
 * managing budgets and deadlines and coordinating relationships with rooms.
 * 
 * Serves as an interface between controllers and the persistence layer.
 * </p>
 */
@Service
public class ProjectService {

    // Project CRUD Interface
    @Autowired
    private final ProjectRepository projectRepository;

    // Client Service layer
    @Autowired
    private final ClientService clientService;

    // Constructor
    public ProjectService(ProjectRepository projectRepository, ClientService clientService) {
        this.projectRepository = projectRepository;
        this.clientService = clientService;

    }

    /**
     * Returns all projects on the system and their room.
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Returns a project using their projectId.
     * 
     * 
     * <p>
     * Fetches a specific project for operations such as updating,
     * assigning rooms or checking project status.
     * </p>
     * 
     * @param id project's unique identifier
     * @throws ProjectNotFoundException if the project is not found
     */
    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("projectId", id));
    }

    /**
     * Finds all projects that have the same status, using the status record.
     * 
     * <p>
     * Retrieves all projects that have the same status for progress tracking.
     * 
     * </p>
     * 
     * 
     * @param status project status enum
     * @returns projects with same status
     */
    public List<Project> getProjectsByStatus(String status) {

        ProjectStatus statusValues = null;

        for (ProjectStatus status1 : ProjectStatus.values()) {
            System.out.println("Checking enum: " + status1.name() + " against input: " + status);
            if (status1.name().equalsIgnoreCase(status.trim())) {
                statusValues = status1;
                break;
            }
        }

        if (statusValues != null) {
            return projectRepository.findProjectsByStatus(statusValues);
        } else {
            throw new ProjectNotFoundException("projectStatus", status);
        }

    }

    /**
     * Returns all projects in the order of due date, using the deadline record.
     * 
     * <p>
     * Sorts all projects in order of due date to help,
     * manage progress tracking and deadlines.
     * 
     * Custom query created in the repository.
     * </p>
     * 
     * @returns order by due date
     */
    public List<Deadline> sortsProjectsByDueDate() {
        return projectRepository.getAllProjectsOrderByDueDate();
    }

    /**
     * Creates a new project for a client.
     * 
     * <p>
     * Creates a new design project for specific client
     * entity for CRUD operations. It will be assigned a unique identifier.
     * </p>
     * 
     * @param project  project object to be created
     * @param clientId client's unique identifier
     * @throws IllegalArgumentException the project fields are null
     */
    public Project createProject(Project project, Long clientId) throws IllegalArgumentException {
        if (project == null && clientId == null) {
            throw new IllegalArgumentException("Project must not be null");
        }
        Client client = clientService.getClient(clientId);
        project.setClient(client);
        return projectRepository.save(project);
    }

    /**
     * Updates a existing project.
     * 
     * <p>
     * Use this method to modify project details such as deadlines, budget or
     * description.
     * 
     * If the project status has been updated to COMPLETED the
     * completedAt field will be set with the current date.
     * </p>
     * 
     * @param id      project's unique identifier
     * @param project project object to be updated
     * @return updated project
     */
    public Project updateProject(Long id, Project project) {

        Project existingProjectId = getProject(id);

        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("projectId", id);
        } else {
            existingProjectId.setProjectName(project.getProjectName());
            existingProjectId.setBudget(project.getBudget());
            existingProjectId.setStatus(project.getStatus());
            existingProjectId.setStartDate(project.getStartDate());
            existingProjectId.setMeetingURL(project.getMeetingURL());
            existingProjectId.setDueDate(project.getDueDate());
            existingProjectId.setClient(project.getClient());

        }
        // Updated Project Status to COMPLETED, sets completedAt field
        if (existingProjectId.getStatus() == ProjectStatus.COMPLETED
                || existingProjectId.getCompletedAt() == null) {
            existingProjectId.setCompletedAt(Instant.now());
        }
        return projectRepository.save(existingProjectId);
    }

    /**
     * Deletes a existing project.
     * 
     * <p>
     * This method is used when a project is canceled and should no longer be
     * tracked. Associated rooms may also be deleted.
     * </p>
     * 
     * @param id project's unique identifier
     * @return project is deleted
     */
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("projectId", id);
        }
        projectRepository.deleteById(id);
    }

    /**
     * Reassigns Project to a existing client.
     * 
     * <p>
     * Use this method when a project was assigned to the wrong client and needs to
     * be reassigned.
     * It will update the many to one relationship.
     * </p>
     * 
     * @param clientId  client's unique identifier
     * @param projectId project's unique identifier
     * @return project is reassigned
     */
    public Project reassignClient(Long clientId, Long projectId) {
        Project existingProjectId = getProject(projectId);
        Client client = clientService.getClient(clientId);

        if (existingProjectId == null || client == null) {
            throw new ProjectNotFoundException("projectId", projectId);
        } else {
            existingProjectId.setClient(client);
        }
        return projectRepository.save(existingProjectId);
    }

    /**
     * Saves project entity when room is created
     * 
     * <p>
     * Creates the One to One relationship
     * with Room, when the room is created.
     * </p>
     *
     * @param project project's object to be saved
     */

    @Transactional
    public Project saveProjectEntity(Project project) {
        return projectRepository.save(project);
    }

}

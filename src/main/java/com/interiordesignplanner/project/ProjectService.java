package com.interiordesignplanner.project;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientService;
import com.interiordesignplanner.exceptions.ProjectNotFoundException;
import com.interiordesignplanner.mapper.ProjectMapper;

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
    private ProjectRepository projectRepository;

    // Client Service layer
    @Autowired
    private final ClientService clientService;

    // Project Mapper
    @Autowired
    private final ProjectMapper projectMapper;

    // Constructor
    public ProjectService(ProjectRepository projectRepository, ClientService clientService,
            ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.clientService = clientService;
        this.projectMapper = projectMapper;
    }

    /**
     * Returns all projects on the system and their room.
     */
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(project -> {
                    ProjectDTO projectDTO = projectMapper.toDto(project);
                    return projectDTO;
                }).toList();
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
    public ProjectDTO getProjectById(Long id) {

        Project project = findProject(id);
        ProjectDTO projectDTO = projectMapper.toDto(project);

        return projectDTO;
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
    public List<ProjectDTO> getProjectsByStatus(ProjectStatus status) {

        if (status == null) {
            throw new ProjectNotFoundException("projectStatus", status);
        }

        return projectRepository.findProjectsByStatus(
                status).stream()
                .map(project -> {
                    ProjectDTO projectDTO = projectMapper.toDto(project);
                    return projectDTO;
                }).toList();

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
    public ProjectDTO createProject(ProjectCreateDTO projectCreateDTO, Long clientId) throws IllegalArgumentException {
        if (projectCreateDTO == null && clientId == null) {
            throw new IllegalArgumentException("Project must not be null");
        }
        Client client = clientService.findClient(clientId);
        projectCreateDTO.setClient(client);
        Project project = projectMapper.toEntity(projectCreateDTO);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
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
    public ProjectDTO updateProject(Long id, ProjectUpdateDTO projectUpdateDTO) {

        Project existingProject = findProject(id);

        // Updated Project Status to COMPLETED, sets completedAt field
        if (existingProject.getStatus() == ProjectStatus.COMPLETED
                && existingProject.getCompletedAt() == null) {
            existingProject.setCompletedAt(Instant.now());
        }

        projectMapper.updateEntity(projectUpdateDTO, existingProject);

        return projectMapper.toDto(projectRepository.save(existingProject));
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
        Project project = findProject(id);
        projectRepository.delete(project);
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
    public ProjectDTO reassignClient(Long clientId, Long projectId) {
        Project existingProject = findProject(projectId);
        Client client = clientService.findClient(clientId);

        if (existingProject == null || client == null) {
            throw new ProjectNotFoundException("projectId", projectId);
        } else {
            existingProject.setClient(client);
        }
        return projectMapper.toDto(projectRepository.save(existingProject));
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

    /**
     * Retrieved the Project's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the project object to be deleted
     * @throws ProjectNotFoundException if the project is not found
     * @return the project
     */
    public Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("projectId", id));
    }

}
